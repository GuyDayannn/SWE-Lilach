package org.cshaifa.spring.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean frozen;

    @ManyToMany
    private List<Store> stores;

    public Customer(String fullName, String username, String email, String password, List<Store> stores, boolean frozen) {
        super(fullName, username, email, password);
        this.stores = stores;
        this.frozen = frozen;
    }

    public Customer() {
        super();
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        this.frozen = true;
    }

    public void unfreeze() {
        this.frozen = false;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

}
