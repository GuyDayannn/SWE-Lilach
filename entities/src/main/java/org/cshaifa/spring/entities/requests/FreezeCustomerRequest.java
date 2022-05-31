package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.Customer;

public class FreezeCustomerRequest extends Request{
    Customer customer;
    boolean toFreeze;

    public FreezeCustomerRequest(Customer customer, boolean toFreeze) {
        this.customer = customer;
        this.toFreeze = toFreeze;
    }
}
