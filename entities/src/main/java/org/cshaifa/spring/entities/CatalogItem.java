package org.cshaifa.spring.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;


import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "catalog_items")
@Where(clause = "DELETED = 0")
public class CatalogItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "DELETED")
    private Integer deleted = 0;

    @Column(name = "item_name")
    private String name;

    private String imagePath;

    private double price;

    private boolean onSale;

    private double discountPercent;

    private String size;

    private String itemType;

    private String itemColor;

    private boolean isDefault;

    @Transient
    private byte[] image = null;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "stock", joinColumns = @JoinColumn(name = "catalog_item_id"))
    @MapKeyJoinColumn(name = "store_id")
    @Column(name = "quantity")
    private Map<Store, Integer> stock;

    public CatalogItem() {
        super();
        this.name = "";
        this.price = 0;
        this.stock = new HashMap<>();
        this.onSale = false;
        this.discountPercent = 0.0;
    }

    public CatalogItem(String name, String imagePath, double price, Map<Store, Integer> quantities, boolean onSale,
            double discountPercent, String size, String itemType, String itemColor, boolean isDefault) {
        super();
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.stock = quantities;
        this.onSale = onSale;
        this.discountPercent = discountPercent;
        this.size = size;
        this.itemType = itemType;
        this.itemColor = itemColor;
        this.isDefault = true;
    }

    public long getId() {
        return id;
    }

    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public double getDiscount() {
        return discountPercent;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean isOnSale) {
        this.onSale = isOnSale;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
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

    public double getFinalPrice() {
        return new BigDecimal(getPrice() * 0.01 * (100 - getDiscount())).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Map<Store, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<Store, Integer> stock) {
        this.stock = stock;
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

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setItemColor(String color) {
        this.itemColor = color;
    }

    public void setDefault(boolean isDefault){
        this.isDefault = isDefault;
    }

    public void reduceQuantity(Store store, int toReduce) {
        if (!stock.containsKey(store) || toReduce > stock.get(store))
            return;

        stock.compute(store, (__, quantity) -> quantity - toReduce);
    }

    @Override
    public boolean equals(Object obj) {
        CatalogItem temp = (CatalogItem) obj;
        if (temp.getId() == this.id)
            return true;
        else if (temp.getName().equals(this.name))
            return true;
        else if (temp.getImagePath().equals(this.imagePath))
            return true;

        return false;
    }
}
