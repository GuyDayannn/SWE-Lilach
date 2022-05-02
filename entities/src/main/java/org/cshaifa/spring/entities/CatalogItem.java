package org.cshaifa.spring.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * This class is a base class for all of the items on the catalog.
 */
@Entity
@Table(name = "catalog_item")
public class CatalogItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name")
    private String name;

    private String imagePath;

    private double price;

    @Transient
    byte[] image = null;

    public CatalogItem() {
        super();
        this.name = "";
        this.price = 0;
    }

    public CatalogItem(String name, String imagePath, double price) {
        super();
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
    }

    public CatalogItem(String name, String imagePath, double price, byte[] image) {
        super();
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.image = image;
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
}
