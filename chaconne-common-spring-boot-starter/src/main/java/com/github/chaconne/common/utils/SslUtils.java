package com.github.chaconne.common.utils;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Description: SslUtils
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public abstract class SslUtils {

    private static final Logger log = LoggerFactory.getLogger(SslUtils.class);

    public SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private TrustManager[] getTrustManager() {

        return new TrustManager[] {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        }};
    }

    public HostnameVerifier getHostnameVerifier() {
        return (s, sslSession) -> true;
    }

    public X509TrustManager getX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException(
                        "Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return trustManager;
    }

}
