package org.cshaifa.spring.client;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmployeeProfileController {

    @FXML
    private Button editCatalogButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button catalogButton;

    @FXML
    private TableView<CatalogItem> catalogTable;

    @FXML
    private Button closeComplaintButton;

    @FXML
    private TextField compensationamount;

    @FXML
    private ComboBox<?> complaintComboBox;

    @FXML
    private TextField complaintStatus;

    @FXML
    private TextArea complaintdescription;

    @FXML
    private TextField complaintresponse;

    @FXML
    private TextField discountAmount;

    @FXML
    private ComboBox<Text> itemComboBox;

    @FXML
    private CheckBox selectAllCheckbox;

    @FXML
    private TableColumn<CatalogItem, Double> discountColumn;

    @FXML
    private TableColumn<CatalogItem, Long> idColumn;

    @FXML
    private TableColumn<CatalogItem, String> itemNameColumn;

    @FXML
    private TableColumn<CatalogItem, Double> itemPriceColumn;

    @FXML
    private TableColumn<CatalogItem, Boolean> onsaleColumn;

    @FXML
    private TableColumn<CatalogItem, Boolean> selectColumn;

    @FXML
    private Button updateSalesButton;

    @FXML
    private Button viewComplaint;

    @FXML
    private Text welcomeText;

    @FXML
    private Label updated_sales_text;


    @FXML
    void cancelUpdate(ActionEvent event) {
        System.out.println("You clicked Cancel");
        discountAmount.setText("");
        //cancelButton.getScene().getWindow().s
                //App.setContent("employee profile");
    }

    @FXML
    void showCatalogIds(ActionEvent event) {
        long selectedId = Long.parseLong(itemComboBox.getValue().toString());


    }

    @FXML
    void closeComplaint(ActionEvent event) {

    }

    @FXML
    void exitProfile(ActionEvent event) throws IOException {
        App.logoutUser();
        App.setWindowTitle("primary");
        App.setContent("primary");
    }

    @FXML
    void goCatalog(ActionEvent event) throws IOException {
        App.setWindowTitle("catalog");
        App.setContent("catalog");
    }

    @FXML
    void openComplaint(ActionEvent event) {

    }

    @FXML
    void selectAll(ActionEvent event) {
        int tableLen = catalogTable.getItems().size();
        for (int i = 0; i < tableLen; i++) {
            catalogTable.getItems().get(i).setDefaultValue(false);  //it's ticked

        }
    }

    @FXML
    void updateSales(ActionEvent event) {

        int tableLen = catalogTable.getItems().size();
        for (int i = 0; i < tableLen; i++) {
            if (catalogTable.getItems().get(i).getIsDefault()) { //it's ticked
                //long itemId = Long.parseLong(selectColumn.getId(i));
                long itemId = catalogTable.getItems().get(i).getId(); //gets row and then gets column in table to access specific cell
                //CatalogItem updatedItem = App.getItemByID(itemId);
                CatalogItem updatedItem = catalogTable.getItems().get(i);
                System.out.printf("catalog item is: %s", updatedItem.getName());
                updatedItem.setOnSale(true);
                updatedItem.setDiscountPercent(Double.parseDouble(discountAmount.getText().strip()));

                if (!discountAmount.getText().isEmpty()) { //TODO: check catalog item isn't empty
                    Task<UpdateItemResponse> updateItemTask = App.createTimedTask(() -> {
                        updatedItem.setOnSale(true);
                        updatedItem.setDiscountPercent(Double.parseDouble(discountAmount.getText().strip()));
                        //updating item on server side and on table - in UI

                       // catalogTable.getItems().get(i).setOnSale(true);
                        //catalogTable.getItems().get(i).setDiscountPercent(Double.parseDouble(discountAmount.getText().strip()));

                        return ClientHandler.updateItem(updatedItem);
                    }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

                    updateItemTask.setOnSucceeded(e2 -> {
                        UpdateItemResponse response2 = updateItemTask.getValue();
                        if (!response2.isSuccessful()) {
                            // TODO: maybe log the specific exception somewhere
                            App.hideLoading();
                            System.err.println("Updating item failed");
                            return;
                        }

                        //App.updateCurrentItemDisplayed(response2.getUpdatedItem());
                        //App.hideLoading();
                        //updateSalesButton.getScene().getWindow().hide();
                    });

                    updateItemTask.setOnFailed(e2 -> {
                        //App.hideLoading();
                        // TODO: maybe properly log it somewhere
                        System.out.println("Update sales item failed!");
                        updated_sales_text.setText(Constants.UPDATED_SALES_ITEM_FAILED);
                        updated_sales_text.setTextFill(Color.RED);
                        updateItemTask.getException().printStackTrace();
                    });

                    new Thread(updateItemTask).start();
                    System.out.println("Update sales item Success!");
                    updated_sales_text.setText(Constants.UPDATED_SALES_ITEM);
                    updated_sales_text.setTextFill(Color.GREEN);
                }
            }


        }
    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser() != null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        } else {
            welcomeText.setText("Welcome, unknown employee");
        }
        //initilization of catalog for table below
        Task<GetCatalogResponse> getCatalogTask = App.createTimedTask(() -> {
            return ClientHandler.getCatalog();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getCatalogTask.setOnSucceeded(e -> {
            if (getCatalogTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }
            GetCatalogResponse response = getCatalogTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }

            catalogTable.setEditable(true);
            List<CatalogItem> catalogItems = response.getCatalogItems();
            ObservableList<CatalogItem> data = FXCollections.observableArrayList();

//            idColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, Long>("id"));
//            itemNameColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, String>("name"));
//            itemPriceColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, Double>("price"));
//            onsaleColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, Boolean>("onSale"));
//            discountColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, Double>("discountPercent"));
//            selectColumn.setCellValueFactory(new PropertyValueFactory<String,ObservableValue<Boolean>>(""));
//

            idColumn.setCellValueFactory(cellData ->
                    new SimpleLongProperty(cellData.getValue().getId()).asObject());

            itemNameColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getName()));

            itemPriceColumn.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());

            onsaleColumn.setCellValueFactory(cellData ->
                    new SimpleBooleanProperty(cellData.getValue().isOnSale()));

            discountColumn.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getDiscount()).asObject());

            selectColumn.setCellValueFactory(cellData ->
                    new SimpleBooleanProperty(cellData.getValue().getIsDefault()));
            selectColumn.setCellFactory(cellData -> new CheckBoxTableCell<>());

            //catalogTable.getColumns().addAll(idColumn,itemNameColumn, itemPriceColumn, onsaleColumn, discountColumn);

            data.addAll(catalogItems);
            catalogTable.setItems(data);

            List<Text> ids = new ArrayList<>();
            for (CatalogItem catalogItem : catalogItems) {
                long id = catalogItem.getId();
                ids.add(new Text(Long.toString(id)));
            }
            ObservableList<Text> itemsIds = FXCollections.observableArrayList(ids);
            itemComboBox.setItems(itemsIds);

            App.hideLoading();
        });

        getCatalogTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getCatalogTask.getException().printStackTrace();
            App.hideLoading();
        });
        new Thread(getCatalogTask).start();

    }
}