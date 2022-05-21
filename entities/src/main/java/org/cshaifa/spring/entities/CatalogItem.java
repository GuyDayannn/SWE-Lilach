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

    private boolean onSale;

    private double discountPercent;

    private boolean defaultValue; //for checkbox

    private String size;
    private String itemType;
    private String itemColor;


    @Transient
    private byte[] image = null;

    private int quantity;

    public CatalogItem() {
        super();
        this.name = "";
        this.price = 0;
        this.quantity = 0;
        this.onSale = false;
        this.discountPercent = 0.0;
        this.defaultValue = true;
    }

//    public CatalogItem(String name, String imagePath, double price, int quantity, boolean onSale, double discountPercent, boolean defaultValue) {
//        super();
//        this.name = name;
//        this.imagePath = imagePath;
//        this.price = price;
//        this.quantity = quantity;
//        this.onSale = onSale;
//        this.discountPercent = discountPercent;
//        this.defaultValue = defaultValue;
//    }

    public CatalogItem(
            String name,
            String imagePath,
            double price,
            int quantity,
            boolean onSale,
            double discountPercent,
            String size,
            String itemType,
            String itemColor,
            boolean defaultValue) {
        super();
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.quantity = quantity;
        this.onSale = onSale;
        this.discountPercent = discountPercent;
        this.size = size;
        this.itemType = itemType;
        this.itemColor = itemColor;
        this.defaultValue = defaultValue;
    }

    public long getId() {
        return id;
    }

    public double getDiscount() {
        return discountPercent;
    }

    public boolean getIsDefault(){return defaultValue;}

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean isOnSale){
        this.onSale = isOnSale;
    }

    public void setDiscountPercent(double discountPercent){
        this.discountPercent = discountPercent;
    }

    public void setDefaultValue(boolean defaultValue){

        this.defaultValue = defaultValue;
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

    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public String getItemColor() {
        return itemColor;
    }
    public void setItemColor(String color) {
        this.itemColor = color;
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
