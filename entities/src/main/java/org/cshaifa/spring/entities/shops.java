package org.cshaifa.spring.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "shops")
public class shops implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;

    @OneToOne
    private Stock myStock;

    public shops() {
        this.name = "";
        this.address = "";

    }

    public shops(String name, String address, Stock myStock) {
        this.name = name;
        this.address = address;
        this.myStock = myStock;
    }

    public int getId() {
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
        return myStock;
    }

    public void setMyStock(Stock myStock) {
        this.myStock = myStock;
    }

}
