package com.github.chaconne.client;

import java.util.List;
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

    private String serviceId = "chaconne-admin-service";
    private String taskGroup;
    private List<String> serverAddresses;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public List<String> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(List<String> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }



}
