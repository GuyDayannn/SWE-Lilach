package org.cshaifa.spring.entities.requests;

import java.util.Map;

import org.cshaifa.spring.entities.Employee;
import org.cshaifa.spring.entities.Store;

public class CreateItemRequest extends Request {

    private String name;

    private double price;

    private Map<Store, Integer> quantities;

    private boolean onSale;

    private double discountPercent;

    private String size;

    private String itemType;

    private String itemColor;

    private boolean isDefault;

    private byte[] image;

    private Employee employee;

    public CreateItemRequest(Employee employee, String name, double price, Map<Store, Integer> quantities, boolean onSale,
            double discountPercent, String size, String itemType, String itemColor, boolean isDefault, byte[] image) {
        this.name = name;
        this.price = price;
        this.quantities = quantities;
        this.onSale = onSale;
        this.discountPercent = discountPercent;
        this.size = size;
        this.itemType = itemType;
        this.itemColor = itemColor;
        this.isDefault = isDefault;
        this.image = image;
        this.employee = employee;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public String getSize() {
        return size;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemColor() {
        return itemColor;
    }

    public byte[] getImage() {
        return image;
    }

    public Map<Store, Integer> getQuantities() {
        return quantities;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Employee getEmployee() {
        return employee;
    }

}
