package org.cshaifa.spring.entities;

import java.sql.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(fetch = FetchType.LAZY)
    private List<CatalogItem> items;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private String greeting;

    private Date orderDate;

    private Date supplyDate;

    private boolean delivery;

    private boolean completed;

    private double total;

    public Order(List<CatalogItem> items, Store store, Customer customer, String greeting, Date orderDate, Date supplyDate,
            boolean delivery) {
        super();
        this.items = items;
        this.store = store;
        this.customer = customer;
        this.greeting = greeting;
        this.orderDate = orderDate;
        this.supplyDate = supplyDate;
        this.delivery = delivery;
        this.completed = false;
        this.total = items.stream().mapToDouble(CatalogItem::getFinalPrice).sum();

    }

    public Order() {
    }


    public List<CatalogItem> getItems() { return items; }

    public void setItems(List<CatalogItem> items) {
        this.items = items;
    }

    public Store getStore() { return store; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getTotal() {
        return total;
    }

}
