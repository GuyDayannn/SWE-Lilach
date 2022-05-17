package org.cshaifa.spring.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "stores")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String address;

    @OneToMany
    private List<CatalogItem> stock;

    @Cascade(CascadeType.ALL)
    @ManyToMany
    private List<Customer> customers;

    public Store() {
        this.name = "";
        this.address = "";
        this.stock = new ArrayList<>();
        this.customers = new ArrayList<>();
    }

    public Store(String name, String address, List<CatalogItem> stock) {
        this.name = name;
        this.address = address;
        this.stock = stock;
        this.customers = new ArrayList<>();
    }

    public Store(String name, String address, List<CatalogItem> stock, List<Customer> customers) {
        this.name = name;
        this.address = address;
        this.stock = stock;
        this.customers = customers;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<CatalogItem> getStock() {
        return stock;
    }

    public void setStock(List<CatalogItem> stock) {
        this.stock = stock;
    }

    public void addCustomer(Customer customer) { this.customers.add(customer); }

    public void addItem(CatalogItem catalogItem) { this.stock.add(catalogItem); }
}
