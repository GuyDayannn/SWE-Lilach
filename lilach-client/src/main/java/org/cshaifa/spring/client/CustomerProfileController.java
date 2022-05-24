package org.cshaifa.spring.client;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.responses.AddComplaintResponse;
import org.cshaifa.spring.entities.responses.GetComplaintsResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CustomerProfileController {

    @FXML
    private Button closeComplaintButton;

    @FXML
    private TextField compensationAmount;

    @FXML
    private TextArea complaintDescription;

    @FXML
    private ListView<Complaint> complaintList;

    @FXML
    private TextField complaintOrderID;

    @FXML
    private TextField complaintResponse;

    @FXML
    private TextField complaintStatus;

    @FXML
    private Button newComplaintButton;

    @FXML
    private ListView<?> orderList;

    @FXML
    private Button signOutButton;

    @FXML
    private Button viewCatalogButton;

    @FXML
    private ComboBox<Text> itemComboBox;

    @FXML
    private Text welcomeText;

    @FXML
    private TableView<Complaint> complaintTable;

    @FXML
    private TableColumn<Complaint, Long> complaintIdColumn;

    @FXML
    private TableColumn<Complaint, Boolean> complaintStatusColumn;

    @FXML
    private TableColumn<Complaint, String> complaintDescriptionColumn;

    @FXML
    private TableColumn<Complaint, String> complaintResponseColumn;

    @FXML
    private TableColumn<Complaint, Double> complaintCompensationAmountColumn;

    @FXML
    private Label invalid_customer_text;


    @FXML
    void closeComplaint(ActionEvent event) {

    }

    @FXML
    void logOut(ActionEvent event) throws IOException {
        App.logoutUser();
        App.setWindowTitle("primary");
        App.setContent("primary");
    }

    @FXML
    void openCatalog(ActionEvent event) throws IOException {
        App.setWindowTitle("catalog");
        App.setContent("catalog");
    }

    @FXML
    void sendComplaint(ActionEvent event) throws ExecutionException, InterruptedException {
        Complaint complaint = new Complaint();
        complaint.setComplaintOpen(true);
        complaint.setComplaintDescription(complaintDescription.getText().strip());
        complaint.setComplaintResponse("");
        if (App.getCurrentUser()!=null) {
            //long custID = App.getCurrentUser().getId();
            //Customer customer = getCustomerbyID(custID);

            if(App.getCurrentUser() instanceof Customer)
            {
                Customer customer = (Customer) App.getCurrentUser();
                System.out.printf("customer is: ",  customer.getUsername());
                System.out.printf("%d%n", customer.getId());

                complaintList.getItems().add(complaint); //adding new complaint in UI

                Task<AddComplaintResponse> addComplaintTask = App.createTimedTask(() -> {
                    complaint.setCustomer(customer);
                    customer.addComplaint(complaint);
                    return ClientHandler.addComplaint(complaintDescription.getText().strip(), customer);
                }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

                addComplaintTask.setOnSucceeded(e2 -> {
                    AddComplaintResponse response2 = addComplaintTask.getValue();
                    if (!response2.isSuccessful()) {
                        // TODO: maybe log the specific exception somewhere
                        App.hideLoading();
                        System.err.println("Add complaint failed!");
                        return;
                    }
                });

                addComplaintTask.setOnFailed(e2 -> {

                    // TODO: maybe properly log it somewhere
                    System.out.println("Add complaint failed!");
//                    updated_sales_text.setText(Constants.UPDATED_SALES_ITEM_FAILED);
//                    updated_sales_text.setTextFill(Color.RED);
                    addComplaintTask.getException().printStackTrace();
                    return;
                });

                new Thread(addComplaintTask).start();
                System.out.println("Add complaint Success!");
//                updated_sales_text.setText(Constants.UPDATED_SALES_ITEM);
//                updated_sales_text.setTextFill(Color.GREEN);
            }
            else{
                invalid_customer_text.setText("failed to get customer ");
                invalid_customer_text.setTextFill(Color.RED);
            }
        }
    }

//    Customer getCustomerbyID(long customerID) throws ExecutionException, InterruptedException {
//        Task<GetCustomerResponse> customerResponseTask = App.createTimedTask(() -> {
//            return ClientHandler.getCustomerResponse(customerID);
//        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);
//
//        customerResponseTask.setOnSucceeded(e -> {
//            if (customerResponseTask.getValue() == null) {
//                invalid_customer_text.setText("failed customer response task");
//                invalid_customer_text.setTextFill(Color.RED);
//                App.hideLoading();
//                return;
//            }
//
//            GetCustomerResponse getCustomerResponse = customerResponseTask.getValue();
//            if (!getCustomerResponse.isSuccessful()) {
//                invalid_customer_text.setText("getCustomerResponse error");
//                invalid_customer_text.setTextFill(Color.RED);
//                App.hideLoading();
//                return;
//            }
//
////            App.setCurrentUser(loginResponse.getUser());
////            App.hideLoading();
//
//        });
//
//        //App.showLoading(rootPane, null, Constants.LOADING_TIMEOUT, TimeUnit.SECONDS);
//        new Thread(customerResponseTask).start();
//        return customerResponseTask.getValue().getCustomer();
//    }


    @FXML
    public void initialize() {
        if (App.getCurrentUser()!=null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        }
        else {
            welcomeText.setText("Welcome, unknown customer");
        }

        Task<GetComplaintsResponse> getComplaintsTask = App.createTimedTask(() -> {
            return ClientHandler.getComplaints();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getComplaintsTask.setOnSucceeded(e -> {
            if (getComplaintsTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }
            GetComplaintsResponse response = getComplaintsTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }

            complaintTable.setEditable(true);
            List<Complaint> complaintList = response.getComplaintList();
            ObservableList<Complaint> data = FXCollections.observableArrayList();

            complaintIdColumn.setCellValueFactory(cellData ->
                    new SimpleLongProperty(cellData.getValue().getId()).asObject());

            complaintStatusColumn.setCellValueFactory(cellData ->
                    new SimpleBooleanProperty(cellData.getValue().getIsComplaintOpen()));

            complaintDescriptionColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getComplaintDescription()));

            complaintResponseColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getComplaintResponse()));

            complaintCompensationAmountColumn.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getCompensationAmount()).asObject());

            data.addAll(complaintList);
            complaintTable.setItems(data);

//            List<Text> ids = new ArrayList<>();
//            for (Complaint complaint : complaintList) {
//                long id = complaint.getId();
//                ids.add(new Text(Long.toString(id)));
//            }
//            ObservableList<Text> itemsIds = FXCollections.observableArrayList(ids);
//            itemComboBox.setItems(itemsIds);

            App.hideLoading();

    });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
            App.hideLoading();
        });
        new Thread(getComplaintsTask).start();

    }
}
