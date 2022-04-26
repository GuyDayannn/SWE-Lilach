package org.cshaifa.spring.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "User")
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String username;
    private String mail;
    private String password;

    @OneToMany
    private List<shops> shops;

    public User(String name, String username, String mail, String password, List<shops> shops) {
        this.name = name;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.shops = shops;
    }

    public User() {
        this.name = "";
        this.username = "";
        this.mail = "";
        this.password = "";
        this.shops = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List getShops() {
        return shops;
    }

    public void setShops(List shops) {
        this.shops = shops;
    }

    @Override
    public boolean equals(Object obj) {
        User temp = (User) obj;
        if(temp.getId() == this.id)
            return true;
        return false;
    }

}
