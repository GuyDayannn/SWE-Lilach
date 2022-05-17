package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.User;

public class RegisterResponse extends Response {

    private String message;

    private User user;

    public RegisterResponse(int requestId, boolean success, String message) {
        super(requestId, success);
        this.message = message;
        this.user = null;
    }

    public RegisterResponse(int requestId, boolean success, String message, User user) {
        super(requestId, success);
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

}
