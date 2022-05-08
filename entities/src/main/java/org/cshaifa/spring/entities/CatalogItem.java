package org.cshaifa.spring.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "catalog_items")
public class CatalogItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name")
    private String name;

    private String imagePath;

    private double price;

    @Transient
    private byte[] image = null;

    private int quantity;

    public CatalogItem() {
        super();
        this.name = "";
        this.price = 0;
        this.quantity = 0;
    }

    public CatalogItem(String name, String imagePath, double price, int quantity) {
        super();
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.quantity = quantity;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public byte[] getImage() {
      return image;
    }

    public void setImage(byte[] image) {
      this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        CatalogItem temp = (CatalogItem) obj;
        if(temp.getId() == this.id)
            return true;
        else if(temp.getName().equals(this.name))
            return true;
        else if(temp.getImagePath().equals(this.imagePath))
            return true;

        return false;
    }
}
