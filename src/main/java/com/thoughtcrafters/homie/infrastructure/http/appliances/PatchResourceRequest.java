package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdate;
import org.hibernate.validator.constraints.NotEmpty;

public class PatchResourceRequest {
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

    public PropertyUpdate toPropertyUpdate() {
        return new PropertyUpdate<String>(nameFrom(path), value);
    }

    private String nameFrom(String path) {
        return path.replaceAll("/", "");
    }

}
