package org.cshaifa.spring.entities.responses;

import java.io.Serializable;

public class Response implements Serializable {
    private boolean success;

    public Response() {
        this.success = false;
    }

    public Response(boolean success) {
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }
}
