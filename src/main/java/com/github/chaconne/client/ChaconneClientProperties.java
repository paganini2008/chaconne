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

    private String baseUrls = "http://localhost:6104";

    public String getBaseUrls() {
        return baseUrls;
    }

    public void setBaseUrls(String baseUrls) {
        this.baseUrls = baseUrls;
    }

}
