package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.domain.appliances.operations.OperationExecution;
import com.thoughtcrafters.homie.domain.appliances.operations.OperationType;
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

    public OperationExecution toOperationExecution() {
        return new OperationExecution(operationTypeFrom(op), path.split("/")[1], value);
    }

    private OperationType operationTypeFrom(String op) {
        switch (op) {
            case "add":
                return OperationType.ADD;
            case "replace":
                return OperationType.UPDATE;
            case "remove":
                return OperationType.REMOVE;
            default:
                return OperationType.UNKNOWN;
        }
    }
}
