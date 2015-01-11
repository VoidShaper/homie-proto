package com.thoughtcrafters.homie.infrastructure.http.appliances;

import org.hibernate.validator.constraints.NotEmpty;

public class ReplacePatchRequest {
    @NotEmpty
    private String op;
    @NotEmpty
    private String path;
    @NotEmpty
    private String value;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
