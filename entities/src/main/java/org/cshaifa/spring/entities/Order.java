package org.cshaifa.spring.entities;

import java.io.Serializable;
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
    @CollectionTable(name = "orders_items", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<CatalogItem, Integer> items;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    private String greeting;

    private Timestamp orderDate;

    private Timestamp supplyDate;

    private boolean delivery;

    private boolean completed;

    private String status;

    @OneToOne
    private Delivery deliveryDetails;

    private double total;

    public Order(Map<CatalogItem, Integer> items, Store store, Customer customer, String greeting, Timestamp orderDate,
            Timestamp supplyDate, boolean delivery, Delivery deliveryDetails) {
        super();
        this.items = items;
        this.store = store;
        this.customer = customer;
        this.greeting = greeting;
        this.orderDate = orderDate;
        this.supplyDate = supplyDate;
        this.delivery = delivery;
        this.completed = false;
        this.status = "Order Accepted";
        this.deliveryDetails = deliveryDetails;
        this.total = items.entrySet().stream().mapToDouble(entry -> entry.getValue() * entry.getKey().getFinalPrice())
                .sum();
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

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
