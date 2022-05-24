package org.cshaifa.spring.entities.requests;

import java.util.List;

import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.SubscriptionType;

public class RegisterRequest extends Request {

    private String fullName;

    private String username;

    private String email;

    private String password;

    private List<Store> stores;

    private SubscriptionType subscriptionType;

    private List<Complaint> complaintList;

    public RegisterRequest(String fullName, String username, String email, String password, List<Store> stores, SubscriptionType subscriptionType, List<Complaint> complaintList) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.stores = stores;
        this.subscriptionType = subscriptionType;
        this.complaintList = complaintList;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Store> getStores() {
        return stores;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }

}
