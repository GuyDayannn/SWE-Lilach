package org.cshaifa.spring.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

    public Store() {
        this.name = "";
        this.address = "";
        this.stock = new ArrayList<>();
    }

    public Store(String name, String address, List<CatalogItem> stock) {
        this.name = name;
        this.address = address;
        this.stock = stock;
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
}
