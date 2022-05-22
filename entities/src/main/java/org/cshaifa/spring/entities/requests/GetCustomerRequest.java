package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.Customer;

public class GetCustomerRequest extends Request {
    private long customerID;
    //private Customer customer;

    public GetCustomerRequest(long customerID) {
        this.customerID = customerID;

    }

    public long getCustomerID() {
        return customerID;
    }

//    public Customer getCustomer(long customerID) {
//        return customer;
//    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }
}
