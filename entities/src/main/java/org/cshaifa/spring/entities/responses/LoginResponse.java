package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.User;

public class LoginResponse extends Response {

    private String message;
    private User user;

    public LoginResponse(int requestId, boolean success, String message, User user) {
        super(requestId, success);
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
