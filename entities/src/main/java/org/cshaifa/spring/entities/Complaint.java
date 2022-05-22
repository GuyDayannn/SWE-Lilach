package org.cshaifa.spring.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "complaints_table")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String complaintDescription;
    private String complaintResponse;
    private double compensationAmount;
    private boolean isComplaintOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;


    public Complaint(){
        super();
        this.complaintDescription = "";
        this.complaintResponse = "";
        this.isComplaintOpen = true;
        this.compensationAmount = 0.0;
        this.customer = new Customer();
    }

    public Complaint(
        String complaintDescription,
        String complaintResponse,
        double compensationAmount,
        boolean isComplaintOpen,
        Customer customer){
        super();
        this.complaintDescription = complaintDescription;
        this.complaintResponse = complaintResponse;
        this.compensationAmount = compensationAmount;
        this.isComplaintOpen = isComplaintOpen;
    }

    public long getId() {
        return id;
    }
    public double getCompensationAmount(){ return compensationAmount;}
    public String getComplaintDescription() {return complaintDescription;}
    public String getComplaintResponse() {return complaintResponse;}
    public boolean getIsComplaintOpen() {return isComplaintOpen;}
    public Customer getCustomer() {return customer;}

    public void setCompensationAmount(double compensationAmount){
        this.compensationAmount = compensationAmount;
    }
    public void setComplaintDescription(String complaintDescription) {this.complaintDescription = complaintDescription;}
    public void setComplaintResponse(String complaintResponse) {this.complaintResponse = complaintResponse;}
    public void setComplaintOpen(boolean isComplaintOpen) {this.isComplaintOpen = isComplaintOpen;}
    public void setCustomer(Customer customer) {this.customer = customer;}


}
