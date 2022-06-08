package org.cshaifa.spring.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

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

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ChainEmployee> employees;

    @OneToOne//(fetch = FetchType.EAGER)
    private StoreManager storeManager;

    @OneToOne//(fetch = FetchType.EAGER)
    private ChainManager chainManager;

    public Store() {
        this.name = "";
        this.address = "";
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.complaints = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.storeManager = new StoreManager();
    }

    public Store(String name, String address, StoreManager storeManager, List<ChainEmployee> employees) {
        this.name = name;
        this.address = address;
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.complaints = new ArrayList<>();
        this.employees = employees;
        this.storeManager = storeManager;
    }

    public Store(String name, String address, List<Customer> customers) {
        this.name = name;
        this.address = address;
        this.customers = customers;
        this.orders = new ArrayList<>();
        this.complaints = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.storeManager = new StoreManager();
    }

    public Store(String name, String address, List<Customer> customers, List<Order> orders,
                 List<Complaint> complaints, List<ChainEmployee> employees, StoreManager storeManager) {
        this.name = name;
        this.address = address;
        this.customers = customers;
        this.orders = orders;
        this.complaints = complaints;
        this.employees = employees;
        this.storeManager = storeManager;
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

    public StoreManager getStoreManager() {return storeManager;}

    public ChainManager getChainManager() {return chainManager;}

    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    public void setChainManager(ChainManager chainManager) {
        this.chainManager = chainManager;
    }

    public void addCustomer(Customer customer) { this.customers.add(customer); }

    public void addOrder(Order order) {orders.add(order);}

    public void addComplaint(Complaint complaint) {complaints.add(complaint);}

    public void setEmployees(List<ChainEmployee> employees) {this.employees = employees;}

    public List<Order> getOrders() {
        return orders;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public List<ChainEmployee> getEmployees() { return employees; }

    public void removeOrder(Order order) {this.orders.remove(order);}

    public void removeEmployee(ChainEmployee chainEmployee) {this.employees.remove(chainEmployee);}

    public void removeManager() {this.storeManager = null;}

    public void removeChainManger() {this.chainManager = null;}

    public void addEmployee(ChainEmployee chainEmployee) {this.employees.add(chainEmployee);}

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
