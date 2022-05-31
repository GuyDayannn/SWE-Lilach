package org.cshaifa.spring.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "complaints_table")
public class Complaint implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String complaintDescription;
    private String complaintResponse;
    private double compensationAmount;
    private boolean isComplaintOpen;
    private Timestamp complaintTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
    //@LazyCollection(LazyCollectionOption.FALSE)

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public Complaint(){
        super();
        this.complaintDescription = "";
        this.complaintResponse = "";
        this.isComplaintOpen = true;
        this.compensationAmount = 0.0;
        this.customer = new Customer();
        this.store = new Store();
        this.complaintTimestamp = null;
    }

    public Complaint(
        String complaintDescription,
        String complaintResponse,
        double compensationAmount,
        boolean isComplaintOpen,
        Customer customer,
        Store store,
        Timestamp complaintTimestamp){
        super();
        this.complaintDescription = complaintDescription;
        this.complaintResponse = complaintResponse;
        this.compensationAmount = compensationAmount;
        this.isComplaintOpen = isComplaintOpen;
        this.customer = customer;
        this.store = store;
        this.complaintTimestamp = complaintTimestamp;
    }

    public long getId() {
        return id;
    }
    public double getCompensationAmount(){ return compensationAmount;}
    public String getComplaintDescription() {return complaintDescription;}
    public String getComplaintResponse() {return complaintResponse;}
    public boolean getIsComplaintOpen() {return isComplaintOpen;}
    public Customer getCustomer() {return customer;}
    public Store getStore() {return store;}
    public Timestamp getComplaintTimestamp() {return complaintTimestamp;}

    public void setCompensationAmount(double compensationAmount){
        this.compensationAmount = compensationAmount;
    }
    public void setComplaintDescription(String complaintDescription) {this.complaintDescription = complaintDescription;}
    public void setComplaintResponse(String complaintResponse) {this.complaintResponse = complaintResponse;}
    public void setComplaintOpen(boolean isComplaintOpen) {this.isComplaintOpen = isComplaintOpen;}
    public void setCustomer(Customer customer) {this.customer = customer;}
    public void setStore(Store store) {this.store = store;}
    public void setComplaintTimestamp(Timestamp timestamp) {this.complaintTimestamp = timestamp;}

}
