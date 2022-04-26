package org.cshaifa.spring.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;

import java.io.IOException;
import java.util.List;

public class updatePopUpController {
    private String newName;

    private Double newPrice;

    @FXML
    private Button btnPopUpCancel;

    @FXML
    private Button btnPopUpUpdate;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemPriceField;

    @FXML
    void updateName() {
        newName = itemNameField.getText();
    }

    @FXML
    void updatePrice() {
        newPrice = Double.parseDouble(itemPriceField.getText());
    }

    @FXML
    void initialize() {
        itemNameField.setText("");
        itemPriceField.setText("");

        itemPriceField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") && !newValue.contains(".")) {
                    itemPriceField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        btnPopUpUpdate.setOnAction(event -> {
            CatalogItem updatedItem = App.getCurrentItemDisplayed();
            if (newName != null) {
                updatedItem.setName(newName);
            }
            if (newPrice != null) {
                updatedItem.setPrice(newPrice);
            }
            //System.out.println( updatedItem.getId() + " " + updatedItem.getName() + " " + updatedItem.getPrice());
            try {
                UpdateItemResponse response = ClientHandler.updateItem(updatedItem);
                if (response.isSuccessful()) {
                    System.out.println("Success!");
                }
                else {
                    System.out.println("Failed!");
                }
                System.out.println("New catalog is:");
                List<CatalogItem> catalog = ClientHandler.getCatalog().getCatalogItems();
                for (CatalogItem item : catalog) {
                    System.out.println(item.getId() + " " + item.getName() + " " + item.getPrice());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("You clicked OK");
            btnPopUpUpdate.getScene().getWindow().hide();
        });

        btnPopUpCancel.setOnAction(event -> {
            System.out.println("You clicked Cancel");
            btnPopUpCancel.getScene().getWindow().hide();
        });
    }
}
