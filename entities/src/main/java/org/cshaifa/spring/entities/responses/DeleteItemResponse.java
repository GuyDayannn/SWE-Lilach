package org.cshaifa.spring.entities.responses;

public class DeleteItemResponse extends Response{
    private String message;

    public DeleteItemResponse(int requestId, boolean success, String message) {
        super(requestId, success);
        this.message = message;
    }
}
