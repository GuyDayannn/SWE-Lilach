package org.cshaifa.spring.entities.responses;

public class FreezeCustomerResponse extends Response{
    String message;

    public FreezeCustomerResponse(int requestId, String message) {
        super(requestId);
        this.message = message;
    }

    public FreezeCustomerResponse(int requestId, boolean success, String message) {
        super(requestId, success);
        this.message = message;
    }

    public String getMessage() { return message; }

}
