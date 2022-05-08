package org.cshaifa.spring.entities.responses;

import java.io.Serializable;

public abstract class Response implements Serializable {
    private boolean success;
    private int requestId;

    public Response(int requestId) {
        this.requestId = requestId;
        this.success = false;
    }

    public Response(int requestId, boolean success) {
        this.requestId = requestId;
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }

    public int getRequestId() {
        return requestId;
    }
}
