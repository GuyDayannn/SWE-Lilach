package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;

import java.util.List;

public class GetCustomerResponse extends Response{
    Customer customer;
    //long customerID;

    public GetCustomerResponse(int requestId, boolean success) {
        super(requestId, success);
        this.customer = null;
    }

    public GetCustomerResponse(int requestId,  boolean success, Customer customer) {
        super(requestId, success);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
