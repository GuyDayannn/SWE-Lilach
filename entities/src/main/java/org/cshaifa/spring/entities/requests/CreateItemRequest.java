package org.cshaifa.spring.entities.requests;

public class CreateItemRequest extends Request {

    private String name;

    private double price;

    private boolean onSale;

    private double discountPercent;

    private String size;

    private String itemType;

    private String itemColor;

    private byte[] image;

    public CreateItemRequest(String name, double price, boolean onSale, double discountPercent,
            String size, String itemType, String itemColor, byte[] image) {
        this.name = name;
        this.price = price;
        this.onSale = onSale;
        this.discountPercent = discountPercent;
        this.size = size;
        this.itemType = itemType;
        this.itemColor = itemColor;
        this.image = image;
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

}
