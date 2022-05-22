package org.cshaifa.spring.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "customers")
public class Customer extends User {

    private boolean frozen;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Store> stores;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> orders;

    private SubscriptionType subscriptionType;

    public Customer(String fullName, String username, String email, String password, String passwordSalt,
            boolean frozen, SubscriptionType subscriptionType) {
        super(fullName, username, email, password, passwordSalt);
        this.stores = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.frozen = frozen;
        this.subscriptionType = subscriptionType;
    }

    public Customer(String fullName, String username, String email, String password, String passwordSalt,
            List<Store> stores, boolean frozen, SubscriptionType subscriptionType) {
        super(fullName, username, email, password, passwordSalt);
        this.stores = stores;
        this.orders = new ArrayList<>();
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

    public List<Order> getOrders() { return orders; }

    public void addOrder(Order order) {this.orders.add(order); }

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
