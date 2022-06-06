package org.cshaifa.spring.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String passwordSalt;

    private boolean loggedIn;

    public User(String fullName, String username, String email, String password, String passwordSalt) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.loggedIn = false;
    }

    public User() {
        this.fullName = "";
        this.username = "";
        this.email = "";
        this.password = "";
        this.passwordSalt = "";
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

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public long getId() {
        return id;
    }

    public void setId (long id) {this.id = id;}

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
