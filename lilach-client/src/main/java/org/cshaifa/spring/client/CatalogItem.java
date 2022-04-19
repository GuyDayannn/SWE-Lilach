package org.cshaifa.spring.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class CatalogItem {
    @FXML    private ImageView itemImage;
    @FXML    private Text itemName;
    @FXML    private Text itemPrice;
    @FXML    private Button showItemButton;

    public CatalogItem() {}

    public CatalogItem(Text itemName, Text itemPrice, ImageView itemImage) {
        setItemName(itemName);
        setItemPrice(itemPrice);
        setItemImage(itemImage);
    }

    private void setItemPrice(Text itemPrice) {
        this.itemPrice = itemPrice;
    }

    private void setItemImage(ImageView itemImage) {
        this.itemImage = itemImage;
    }

    public void setItemName(Text itemName) {
        this.itemName = itemName;
    }

    public Text getItemPrice() {
        return this.itemPrice;
    }

    public Text getItemName() {
        return this.itemName;
    }

    public ImageView getItemImage() {
        return this.itemImage;
    }
}
