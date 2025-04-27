package com.github.chaconne.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @Description: ChaconneClientProperties
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
@ConfigurationProperties("chaconne.client")
public class ChaconneClientProperties {

    private String baseUrl = "http://localhost:6142";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
