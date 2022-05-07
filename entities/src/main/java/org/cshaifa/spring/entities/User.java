package org.cshaifa.spring.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;
    private String username;
    private String mail;
    private String password;

    @OneToMany
    private List<Store> stores;

    public User(String name, String username, String mail, String password, List<Store> shops) {
        this.fullName = name;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.stores = shops;
    }

    public User() {
        this.fullName = "";
        this.username = "";
        this.mail = "";
        this.password = "";
        this.stores = null;
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

    public List getStores() {
        return stores;
    }

    public void setStores(List shops) {
        this.stores = shops;
    }

    @Override
    public boolean equals(Object obj) {
        User temp = (User) obj;
        if(temp.getId() == this.id)
            return true;
        return false;
    }

}
