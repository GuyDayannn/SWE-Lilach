package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.ChainEmployee;

public class EditEmployeeResponse extends Response{
    private String message;
    //private ChainEmployee chainEmployee = null;

    public EditEmployeeResponse(int requestID, String message){
        super(requestID);
        this.message = message;

    }
    public EditEmployeeResponse(int requestId, boolean success, String message) {
        super(requestId, success);
        this.message = message;
    }

    public String getMessage() { return message; }

}
