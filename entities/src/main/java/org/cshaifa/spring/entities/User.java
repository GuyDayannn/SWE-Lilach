package org.cshaifa.spring.entities;

import java.io.Serializable;

public class User implements Serializable {

    private String fullName;

    private String username;

    private String email;

    private String password;

    private boolean loggedIn;

    public User(String fullName, String username, String email, String password) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.loggedIn = false;
    }

    public User() {
        this.fullName = "";
        this.username = "";
        this.email = "";
        this.password = "";
        this.loggedIn = false;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void login() {
        this.loggedIn = true;
    }

    public void logout() {
        this.loggedIn = false;
    }

}
