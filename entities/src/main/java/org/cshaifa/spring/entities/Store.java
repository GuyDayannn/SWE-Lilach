package org.cshaifa.spring.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "stores")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String address;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Customer> customers;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> orders;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Complaint> complaints;

    public Store() {
        this.name = "";
        this.address = "";
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.complaints = new ArrayList<>();
    }

    public Store(String name, String address) {
        this.name = name;
        this.address = address;
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.complaints = new ArrayList<>();
    }

    public Store(String name, String address, List<Customer> customers) {
        this.name = name;
        this.address = address;
        this.customers = customers;
        this.orders = new ArrayList<>();
        this.complaints = new ArrayList<>();
    }

    public Store(String name, String address, List<Customer> customers, List<Order> orders,List<Complaint> complaints) {
        this.name = name;
        this.address = address;
        this.customers = customers;
        this.orders = orders;
        this.complaints = complaints;
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

    public void addCustomer(Customer customer) { this.customers.add(customer); }

    public void addOrder(Order order) {orders.add(order);}

    public void addComplaint(Complaint complaint) {complaints.add(complaint);}

    public List<Order> getOrders() {
        return orders;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void removeOrder(Order order) {this.orders.remove(order);}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return id == store.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
