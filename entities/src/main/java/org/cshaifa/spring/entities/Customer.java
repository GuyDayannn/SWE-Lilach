package org.cshaifa.spring.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends User {

    private boolean frozen;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Store> stores;

    private SubscriptionType subscriptionType;

    public Customer(String fullName, String username, String email, String password, String passwordSalt,
            boolean frozen, SubscriptionType subscriptionType) {
        super(fullName, username, email, password, passwordSalt);
        this.stores = new ArrayList<>();
        this.frozen = frozen;
        this.subscriptionType = subscriptionType;
    }

    public Customer(String fullName, String username, String email, String password, String passwordSalt,
            List<Store> stores, boolean frozen, SubscriptionType subscriptionType) {
        super(fullName, username, email, password, passwordSalt);
        this.stores = stores;

        this.frozen = frozen;
        this.subscriptionType = subscriptionType;
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

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

}
