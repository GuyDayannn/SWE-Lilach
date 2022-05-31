package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;

public class AddComplaintRequest extends Request {
    private Customer customer;
    private String complaintDescription;
    private Store store;

    Complaint complaint;

    public AddComplaintRequest(String complaintDescription, Customer customer, Store store) {

        this.complaintDescription = complaintDescription;
        this.customer = customer;
        this.store = store;

    }

    public Customer getCustomer(){ return this.customer;}

    public Store getStore() {return this.store;}

    public String getComplaintDescription(){ return this.complaintDescription;}

}
