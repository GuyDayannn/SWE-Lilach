package org.cshaifa.spring.entities.responses;

public class IsAliveResponse extends Response {

    public IsAliveResponse(int requestId) {
        super(requestId, true);
    }

}
