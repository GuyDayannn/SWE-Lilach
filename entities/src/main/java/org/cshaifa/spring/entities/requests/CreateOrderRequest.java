package org.cshaifa.spring.entities.requests;

import java.sql.Timestamp;
import java.util.Map;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Delivery;
import org.cshaifa.spring.entities.Store;

public class CreateOrderRequest extends Request {

    private Store store;

    private Customer customer;

    private String cardNumber;

    private Map<CatalogItem, Integer> items;

    private String greeting;

    private Timestamp orderDate;

    private Timestamp supplyDate;

    private boolean delivery;

    private Delivery deliveryDetails;

    public CreateOrderRequest(Store store, Customer customer, String cardNumber, Map<CatalogItem, Integer> items, String greeting, Timestamp orderDate,
            Timestamp supplyDate, boolean delivery, Delivery deliveryDetails) {
        this.store = store;
        this.customer = customer;
        this.cardNumber = cardNumber;
        this.items = items;
        this.greeting = greeting;
        this.orderDate = orderDate;
        this.supplyDate = supplyDate;
        this.delivery = delivery;
        this.deliveryDetails = deliveryDetails;
    }

    public Store getStore() {
        return store;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Map<CatalogItem, Integer> getItems() {
        return items;
    }

    public String getGreeting() {
        return greeting;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public Timestamp getSupplyDate() {
        return supplyDate;
    }

    public boolean getDelivery() {
        return delivery;
    }

    public Delivery getDeliveryDetails() { return deliveryDetails; }

    public String getCardNumber() { return cardNumber; }
}
