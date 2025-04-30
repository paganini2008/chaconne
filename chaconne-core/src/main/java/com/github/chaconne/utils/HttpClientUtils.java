package com.github.chaconne.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * @Description: HttpClientUtils
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public abstract class HttpClientUtils {

    private static final ConnectionPool CONNECTION_POOL =
            new ConnectionPool(200, 10, TimeUnit.MINUTES);

    private static final OkHttpClient CLIENT =
            new OkHttpClient.Builder().connectionPool(CONNECTION_POOL)
                    .connectTimeout(10, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();

    public static String sendRequest(String url, String httpMethod, Map<String, String> httpHeaders,
            String dataType, String data) throws IOException {

        RequestBody body = null;
        if ("POST".equalsIgnoreCase(httpMethod) || "PUT".equalsIgnoreCase(httpMethod)
                || "DELETE".equalsIgnoreCase(httpMethod)) {
            MediaType mediaType = getMediaType(dataType);
            body = RequestBody.create(data != null ? data : "", mediaType);
        }
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (httpHeaders != null) {
            httpHeaders.forEach(requestBuilder::addHeader);
        }

        switch (httpMethod.toUpperCase()) {
            case "GET":
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.post(body);
                break;
            case "PUT":
                requestBuilder.put(body);
                break;
            case "DELETE":
                requestBuilder.delete(body);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }
        Request request = requestBuilder.build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected HTTP code: " + response.code());
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    public static String uploadFile(String url, File file, String data, String dataType,
            Map<String, String> headers) throws IOException {
        MediaType fileMediaType = MediaType.parse("application/octet-stream");
        MediaType dataMediaType = getMediaType(dataType);

        MultipartBody.Builder multipartBuilder =
                new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBuilder.addFormDataPart("file", file.getName(),
                RequestBody.create(file, fileMediaType));
        if (data != null) {
            multipartBuilder.addFormDataPart("data", null, RequestBody.create(data, dataMediaType));
        }
        Request.Builder requestBuilder =
                new Request.Builder().url(url).post(multipartBuilder.build());
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected HTTP code: " + response.code());
            }
            return response.body() != null ? response.body().string() : null;
        }
    }


    private static MediaType getMediaType(String dataType) {
        if ("json".equalsIgnoreCase(dataType)) {
            return MediaType.parse("application/json; charset=utf-8");
        } else if ("xml".equalsIgnoreCase(dataType)) {
            return MediaType.parse("application/xml; charset=utf-8");
        } else if ("form".equalsIgnoreCase(dataType)) {
            return MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        } else {
            return MediaType.parse("text/plain; charset=utf-8");
        }
    }

    public static void main(String[] args) throws IOException {
        // String url = "https://httpbin.org/post";
        // String method = "POST";
        // String dataType = "json";
        // String data = "{\"name\": \"ChatGPT\", \"lang\": \"Java\"}";
        //
        // Map<String, String> headers = new HashMap<>();
        // headers.put("Authorization", "Bearer fake-token");
        // headers.put("Custom-Header", "demo");
        //
        // try {
        // String response = HttpClientUtils.sendRequest(url, method, headers, dataType, data);
        // System.out.println("Response:\n" + response);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // File file = new File("F:\\test.txt");
        // String data = "{\"name\": \"ChatGPT\", \"lang\": \"Java\"}";
        // String dataType = "json";
        // String response = uploadFile("https://httpbin.org/post", file, data, dataType,
        // Map.of("Authorization", "Bearer 123456"));
        // System.out.println("Response:\n" + response);

        // 模拟的 XML 字符串
        String xmlData = """
                <data>
                    <user>
                        <name>ChatGPT</name>
                        <role>AI</role>
                    </user>
                </data>
                """;

        File file = new File("F:\\test.txt"); // 本地任意存在的文件

        String response = uploadFile("https://httpbin.org/post", file, xmlData, "xml",
                Map.of("Custom-Header", "demo-value"));

        System.out.println("Server Response:\n" + response);
    }
}
