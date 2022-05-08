package org.cshaifa.spring.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends User {
    @ManyToMany
    private List<Store> stores;

    public Customer(String name, String username, String mail, String password, List<Store> stores) {
        super(name, username, mail, password);
        this.stores = stores;
    }

    public Customer(List<Store> stores) {
        this.stores = stores;
    }
}
