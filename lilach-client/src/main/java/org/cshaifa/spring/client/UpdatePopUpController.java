package org.cshaifa.spring.client;

import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class UpdatePopUpController {
    @FXML
    private Button btnPopUpCancel;

    @FXML
    private Button btnPopUpUpdate;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemPriceField;

    @FXML
    private AnchorPane rootPane;

    @FXML
    void initialize() {
        itemNameField.setText("");
        itemPriceField.setText("");

        itemPriceField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    itemPriceField.setText(oldValue);
                }
            }
        });

        btnPopUpUpdate.setOnAction(event -> {
            Task<UpdateItemResponse> updateItemTask = App.createTimedTask(() -> {
                CatalogItem updatedItem = App.getCurrentItemDisplayed();
                if (!itemNameField.getText().isEmpty())
                    updatedItem.setName(itemNameField.getText());
                if (!itemPriceField.getText().isEmpty())
                    updatedItem.setPrice(Double.parseDouble(itemPriceField.getText()));

                return ClientHandler.updateItem(updatedItem);
            }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

            updateItemTask.setOnSucceeded(e -> {
                UpdateItemResponse response = updateItemTask.getValue();
                if (!response.isSuccessful()) {
                    // TODO: maybe log the specific exception somewhere
                    App.hideLoading();
                    System.err.println("Updating item failed");
                    return;
                }

                App.updateCurrentItemDisplayed(response.getUpdatedItem());
                App.hideLoading();
                btnPopUpUpdate.getScene().getWindow().hide();
            });

            updateItemTask.setOnFailed(e -> {
                App.hideLoading();
                // TODO: maybe properly log it somewhere
                updateItemTask.getException().printStackTrace();
            });

            Stage rootStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            App.showLoading(rootPane, rootStage, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
            new Thread(updateItemTask).start();
        });

        btnPopUpCancel.setOnAction(event -> {
            btnPopUpCancel.getScene().getWindow().hide();
        });
    }
}
