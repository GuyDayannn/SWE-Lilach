package org.cshaifa.spring.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.utils.Constants;

import javax.xml.catalog.Catalog;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class EmployeeProfileController {

    @FXML private Button editCatalogButton;

    @FXML private Button exitButton;

    @FXML private Button cancelButton;

    @FXML private Button catalogButton;

    @FXML private TableView<CatalogItem> catalogTable;

    @FXML private Button closeComplaintButton;

    @FXML private TextField compensationamount;

    @FXML private ComboBox<?> complaintComboBox;

    @FXML private TextField complaintStatus;

    @FXML private TextArea complaintdescription;

    @FXML private TextField complaintresponse;

    @FXML private TextField discountAmount;

    @FXML private ComboBox<Text> itemComboBox;

    @FXML private CheckBox selectAllCheckbox;

    @FXML private TableColumn<CatalogItem, String> discountColumn;

    @FXML private TableColumn<CatalogItem, String> idColumn;

    @FXML private TableColumn<CatalogItem, String> itemNameColumn;

    @FXML private TableColumn<CatalogItem, String> itemPriceColumn;

    @FXML private TableColumn<CatalogItem, String> onsaleColumn;

    @FXML private TableColumn<CatalogItem, CheckBoxTableCell> selectColumn;

    @FXML private Button updateSalesButton;

    @FXML private Button viewComplaint;

    @FXML private Text welcomeText;

    @FXML
    void cancelUpdate(ActionEvent event) {

    }

    @FXML
    void closeComplaint(ActionEvent event) {

    }

    @FXML
    void exitProfile(ActionEvent event) throws IOException {
        App.setCurrentUser(null);
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

    }

    @FXML
    void updateSales(ActionEvent event) {

    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser()!=null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        }
        else {
            welcomeText.setText("Welcome, unknown employee");
        }


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

            List<CatalogItem> catalogItems = response.getCatalogItems();

            idColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, String>("id"));
            itemNameColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, String>("name"));
            itemPriceColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, String>("price"));
            onsaleColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, String>("onSale"));
            discountColumn.setCellValueFactory(new PropertyValueFactory<CatalogItem, String>("discountPercent"));

        });
    }

}
