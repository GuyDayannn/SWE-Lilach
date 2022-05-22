package org.cshaifa.spring.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends User {

    private boolean frozen;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Store> stores;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Complaint> complaintList;

    private SubscriptionType subscriptionType;

    public Customer(String fullName, String username, String email, String password, String passwordSalt,
            boolean frozen, SubscriptionType subscriptionType) {
        super(fullName, username, email, password, passwordSalt);
        this.stores = new ArrayList<>();
        this.frozen = frozen;
        this.subscriptionType = subscriptionType;
        this.complaintList = new ArrayList<>();
    }

    public Customer(String fullName, String username, String email, String password, String passwordSalt,
            List<Store> stores, boolean frozen, SubscriptionType subscriptionType, List<Complaint> complaintList) {
        super(fullName, username, email, password, passwordSalt);
        this.stores = stores;
        this.complaintList = complaintList;
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


    public void addComplaint(Complaint complaint) { this.complaintList.add(complaint); }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }
}
