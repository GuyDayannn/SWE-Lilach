package org.cshaifa.spring.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "orders_items", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<CatalogItem, Integer> items;

    @ManyToOne
    private Store store;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    private String cardNumber;

    private String greeting;

    private Timestamp orderDate;

    private Timestamp supplyDate;

    private boolean delivery;

    private boolean completed;

    private String status;

    @OneToOne
    private Delivery deliveryDetails;

    private double total;

    @Transient
    private final double DELIVERY_PRICE = 30;

    public Order(Map<CatalogItem, Integer> items, Store store, Customer customer, String cardNumber, String greeting, Timestamp orderDate,
            Timestamp supplyDate, boolean delivery, Delivery deliveryDetails) {
        super();
        this.items = items;
        this.store = store;
        this.customer = customer;
        this.cardNumber = cardNumber;
        this.greeting = greeting;
        this.orderDate = orderDate;
        this.supplyDate = supplyDate;
        this.delivery = delivery;
        this.completed = false;
        this.status = "Order Accepted";
        this.deliveryDetails = deliveryDetails;
        this.total = items.entrySet().stream().mapToDouble(entry -> entry.getValue() * entry.getKey().getFinalPrice())
                .sum() + (delivery ? DELIVERY_PRICE : 0);
    }

    public Order() {
    }

    public Map<CatalogItem, Integer> getItems() {
        return items;
    }

    public void setItems(Map<CatalogItem, Integer> items) {
        this.items = items;
    }

    public Store getStore() {
        return store;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCardNumber() { return cardNumber; }

    public void setCardNumber(String creditCard) { this.cardNumber = creditCard; }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Timestamp getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(Timestamp supplyDate) {
        this.supplyDate = supplyDate;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public Delivery getDeliveryDetails() { return deliveryDetails; }

    public void setDelivery(boolean delivery) { this.delivery = delivery; }

    public void setDeliveryDetails(Delivery deliveryDetails) { this.deliveryDetails = deliveryDetails; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public void deleteCustomer() { this.customer = null; }

    public double getTotal() { return total; }

    public long getId() {
        return id;
    }

    public double getRefundAmount(Timestamp cancelTime) {
        if (cancelTime.after(supplyDate)) {
            return 0;
        }
        System.out.println("Supply Date:\t" + supplyDate);
        long milliseconds = supplyDate.getTime() - cancelTime.getTime();
        int seconds = (int) milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;

        if (hours >= 3) {
            return total;
        }
        else if (hours >= 1) {
            return 0.5*total;
        }
        else if (hours < 0) {   // overflow
            return total;
        }
        else return 0;
    }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
