package org.cshaifa.spring.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "store")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String address;

    @OneToOne
    private Stock stock;

    public Store() {
        this.name = "";
        this.address = "";

    }

    public Store(String name, String address, Stock myStock) {
        this.name = name;
        this.address = address;
        this.stock = myStock;
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

    public Stock getMyStock() {
        return stock;
    }

    public void setMyStock(Stock myStock) {
        this.stock = myStock;
    }

}
