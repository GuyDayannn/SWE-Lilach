package org.cshaifa.spring.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    private String fullName;
    private String username;
    private String mail;
    private String password;

    public User(String name, String username, String mail, String password) {
        this.fullName = name;
        this.username = username;
        this.mail = mail;
        this.password = password;
    }

    public User() {
        this.fullName = "";
        this.username = "";
        this.mail = "";
        this.password = "";
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        User temp = (User) obj;
        if(temp.getId() == this.id)
            return true;
        return false;
    }

}
