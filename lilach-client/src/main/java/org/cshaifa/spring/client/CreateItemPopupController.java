
package org.cshaifa.spring.client;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.cshaifa.spring.entities.Employee;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.responses.CreateItemResponse;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.ImageUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CreateItemPopupController {
    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemPriceField;

    @FXML
    private TextField itemDiscountField;

    @FXML
    private TextField itemQuantityField;

    @FXML
    private ComboBox<String> storeSelector;

    @FXML
    private ComboBox<String> itemTypeSelector;

    @FXML
    private ComboBox<String> itemSizeSelector;

    @FXML
    private ComboBox<String> itemColorSelector;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView selectedImageView;

    private List<Store> stores = null;

    private File selectedImage = null;

    private Map<Store, Integer> quantities = new HashMap<>();

    @FXML
    void initialize() {

        itemSizeSelector.getItems().addAll("small", "medium", "large");
        itemColorSelector.getItems().addAll("red", "orange", "pink");
        itemTypeSelector.getItems().addAll("flower", "bouquet", "plant", "orchid", "wine", "chocolate", "set");

        itemPriceField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    itemPriceField.setText(oldValue);
                }
            }
        });

        itemDiscountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                itemDiscountField.setText(oldValue);
        });

        itemQuantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                itemQuantityField.setText(oldValue);
        });

        Task<GetStoresResponse> getStoresTask = App.createTimedTask(ClientHandler::getStores, Constants.REQUEST_TIMEOUT,
                TimeUnit.SECONDS);

        getStoresTask.setOnSucceeded(e -> {
            GetStoresResponse getStoresResponse = getStoresTask.getValue();

            if (getStoresResponse == null || !getStoresResponse.isSuccessful()) {
                errorLabel.setText("Failed retrieving stores");
                errorLabel.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            stores = getStoresResponse.getStores().stream()
                    .filter(store -> !store.getName().equals(Constants.WAREHOUSE_NAME)).toList();
            storeSelector.getItems().addAll(stores.stream().map(Store::getName).toList());
            storeSelector.getSelectionModel().selectedIndexProperty().addListener((options, oldValue, newValue) -> {
                if (oldValue.intValue() == -1)
                    return;

                if (itemQuantityField.getText().isBlank())
                    quantities.remove(stores.get(oldValue.intValue()));
                else
                    quantities.put(stores.get(oldValue.intValue()), Integer.parseInt(itemQuantityField.getText()));

                if (quantities.containsKey(stores.get(newValue.intValue())))
                    itemQuantityField.setText(Integer.toString(quantities.get(stores.get(newValue.intValue()))));
                else
                    itemQuantityField.clear();
            });
            App.hideLoading();
        });

        getStoresTask.setOnFailed(e -> {
            getStoresTask.getException().printStackTrace();
            errorLabel.setText("Failed retrieving stores");
            errorLabel.setTextFill(Color.RED);
            App.hideLoading();
        });

        App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(getStoresTask).start();
    }

    @FXML
    private void cancel(ActionEvent event) {
        App.setCreatedItem(null);
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void chooseImage(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        ExtensionFilter filter = new ExtensionFilter("JPG files (*.jpg)", "*.jpeg", "*.jpg", "*.JPG");
        chooser.getExtensionFilters().add(filter);
        selectedImage = chooser.showOpenDialog(null);
        if (selectedImage != null)
            selectedImageView.setImage(new Image(selectedImage.toURI().toString()));
    }

    @FXML
    private void createItem(ActionEvent event) {
        if (selectedImage == null || itemNameField.getText().isBlank() || itemPriceField.getText().isBlank()
                || itemSizeSelector.getSelectionModel().getSelectedIndex() == -1
                || itemColorSelector.getSelectionModel().getSelectedIndex() == -1
                || itemTypeSelector.getSelectionModel().getSelectedIndex() == -1) {
            errorLabel.setText("You must fill out all required fields");
            errorLabel.setTextFill(Color.RED);
            return;
        }

        int selectedStoreIndex = storeSelector.getSelectionModel().getSelectedIndex();
        if (selectedStoreIndex != -1) {
            if (itemQuantityField.getText().isBlank())
                quantities.remove(stores.get(selectedStoreIndex));
            else
                quantities.put(stores.get(selectedStoreIndex), Integer.parseInt(itemQuantityField.getText()));
        }

        Task<CreateItemResponse> createItemTask = App
                .createTimedTask(
                        () -> ClientHandler.createItem((Employee) App.getCurrentUser(), itemNameField.getText(),
                                Double.parseDouble(itemPriceField.getText()), quantities,
                                !itemDiscountField.getText().isBlank(),
                                !itemDiscountField.getText().isBlank() ? Integer.parseInt(itemDiscountField.getText())
                                        : 0,
                                itemSizeSelector.getSelectionModel().getSelectedItem(),
                                itemTypeSelector.getSelectionModel().getSelectedItem(),
                                itemColorSelector.getSelectionModel().getSelectedItem(), true,
                                ImageUtils.getByteArrayFromURI(selectedImage.toURI())),
                        Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        createItemTask.setOnSucceeded(e -> {
            CreateItemResponse createItemResponse = createItemTask.getValue();
            if (createItemResponse == null || !createItemResponse.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                errorLabel.setText("Creating Item failed");
                errorLabel.setTextFill(Color.RED);
                App.hideLoading();
                return;
            }

            App.setCreatedItem(createItemResponse.getItem());
            App.hideLoading();
            ((Button) event.getSource()).getScene().getWindow().hide();
        });

        createItemTask.setOnFailed(e -> {
            App.hideLoading();
            // TODO: maybe properly log it somewhere
            createItemTask.getException().printStackTrace();
        });

        Stage rootStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        App.showLoading(rootPane, rootStage, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
        new Thread(createItemTask).start();
    }
}
