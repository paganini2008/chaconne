package com.github.chaconne.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * 
 * @Description: LoggingRequestInterceptor
 * @Author: Fred Feng
 * @Date: 25/12/2024
 * @Version 1.0.0
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
    private static final Marker logMarker = MarkerFactory.getMarker("syslog");
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final int maxLengthOfResponseBodyString = 1024;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        String handlerDescription = request.getURI().toString();
        StringBuilder str = new StringBuilder();
        str.append(NEWLINE);
        log(str, "[%s] <--- HTTP/1.1 %s %s ", handlerDescription, request.getMethod().name(),
                request.getURI().getPath());
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            log(str, "[%s] %s: %s", handlerDescription, entry.getKey(), entry.getValue());
        }
        log(str, "[%s] request body: %s", handlerDescription, new String(body));
        long startTime = System.currentTimeMillis();
        Exception reason = null;
        ClientHttpResponse response;
        try {
            response = execution.execute(request, body);
            log(str, "[%s] <--- END HTTP %s (%s ms) ", handlerDescription, response.getStatusCode(),
                    System.currentTimeMillis() - startTime);
            MediaType mediaType = response.getHeaders().getContentType();
            String responseBodyString =
                    isHtmlResponseMediaType(mediaType) || isRestfulResponseMediaType(mediaType)
                            ? IOUtils.toString(response.getBody(), Charset.defaultCharset())
                            : "";
            responseBodyString = trimResponseBodyString(responseBodyString);
            log(str, "[%s] <--- response body: %s (%s bytes) ", handlerDescription,
                    responseBodyString, response.getBody().available());
        } catch (IOException e) {
            reason = e;
            throw e;
        } finally {
            if (reason != null) {
                log(str, "[%s] <--- ERROR %s: %s", handlerDescription,
                        reason.getClass().getSimpleName(), reason.getMessage());
                StringWriter sw = new StringWriter();
                reason.printStackTrace(new PrintWriter(sw));
                log(str, "[%s] %s", handlerDescription, sw.toString());
                log(str, "[%s] <--- END ERROR", handlerDescription);
            }
            if (log.isTraceEnabled()) {
                log.trace(logMarker, str.toString());
            }
        }
        return response;
    }

    private boolean isHtmlResponseMediaType(MediaType mediaType) {
        return MediaType.TEXT_HTML.equals(mediaType) || MediaType.TEXT_PLAIN.equals(mediaType);
    }

    private boolean isRestfulResponseMediaType(MediaType mediaType) {
        return MediaType.APPLICATION_JSON.equals(mediaType)
                || MediaType.APPLICATION_JSON_UTF8.equals(mediaType)
                || MediaType.APPLICATION_XML.equals(mediaType)
                || MediaType.APPLICATION_GRAPHQL.equals(mediaType);
    }

    private void log(StringBuilder str, String format, Object... args) {
        str.append(String.format(format, args));
        str.append(NEWLINE);
    }

    private String trimResponseBodyString(String responseBodyString) {
        if (StringUtils.isBlank(responseBodyString)) {
            return "";
        }
        if (responseBodyString.length() <= maxLengthOfResponseBodyString) {
            return responseBodyString;
        }
        return responseBodyString.substring(0, maxLengthOfResponseBodyString)
                .concat(" Omitted ...");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
