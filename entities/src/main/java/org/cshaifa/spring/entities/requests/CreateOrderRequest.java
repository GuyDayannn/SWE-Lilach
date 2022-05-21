package org.cshaifa.spring.entities.requests;

import java.sql.Date;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Store;

public class CreateOrderRequest extends Request {

    private Store store;

    private Customer customer;

    private List<CatalogItem> items;

    private String greeting;

    private Date orderDate;

    private Date supplyDate;

    public CreateOrderRequest(Store store, Customer customer, List<CatalogItem> items, String greeting, Date orderDate,
            Date supplyDate) {
        this.store = store;
        this.customer = customer;
        this.items = items;
        this.greeting = greeting;
        this.orderDate = orderDate;
        this.supplyDate = supplyDate;
    }

    public Store getStore() {
        return store;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CatalogItem> getItems() {
        return items;
    }

    public String getGreeting() {
        return greeting;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Date getSupplyDate() {
        return supplyDate;
    }

}
