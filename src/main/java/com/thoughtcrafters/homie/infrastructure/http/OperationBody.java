package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.Operation;

import javax.ws.rs.core.UriBuilder;

public class OperationBody {
    private String url;
    private String method;
    private String description;

    public OperationBody() {

    }

    private OperationBody(String url, String method, String description) {
        this.url = url;
        this.method = method;
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static OperationBody from(ApplianceId applianceId, Operation operation) {
        return new OperationBody(
                UriBuilder.fromPath("/appliances")
                          .path(applianceId.uuid().toString())
                          .path(operation.path())
                          .build()
                          .toString(),
                "POST",
                operation.description());
    }
}
