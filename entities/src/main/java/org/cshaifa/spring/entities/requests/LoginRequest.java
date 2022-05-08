package org.cshaifa.spring.entities.requests;

public class LoginRequest extends Request {

    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
