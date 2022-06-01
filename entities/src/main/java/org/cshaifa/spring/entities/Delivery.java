package org.cshaifa.spring.entities;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "deliveries")
public class Delivery implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String recipientName;

    private String phoneNumber;

    private String address;

    private String message;

    private boolean immediate;

    private boolean deliveryCompleted;


    public Delivery(String recipientName, String phoneNumber, String address, String message, boolean immediate, boolean deliveryCompleted) {
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.message = message;
        this.immediate = immediate;
        this.deliveryCompleted = deliveryCompleted;
    }

    public Delivery() {
    }

    public String getRecipientName() { return recipientName; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getAddress() { return address; }

    public String getMessage() { return message; }

    public boolean isDeliveryCompleted() { return deliveryCompleted; }

    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setAddress(String address) { this.address = address; }

    public void setMessage(String message) { this.message = message; }

    public void setDeliveryCompleted(boolean deliveryCompleted) { this.deliveryCompleted = deliveryCompleted; }
}
