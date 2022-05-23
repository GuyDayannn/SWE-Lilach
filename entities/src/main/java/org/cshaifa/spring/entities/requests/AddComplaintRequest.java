package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;

public class AddComplaintRequest extends Request {
    private Customer customer;
    private String complaintDescription;

    Complaint complaint;

    public AddComplaintRequest(String complaintDescription, Customer customer) {

        this.complaintDescription = complaintDescription;
        this.customer = customer;
    }

    public Customer getCustomer(){ return this.customer;}

    public String getComplaintDescription(){ return this.complaintDescription;}

}
