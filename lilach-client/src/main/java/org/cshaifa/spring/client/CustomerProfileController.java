package org.cshaifa.spring.client;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Order;
import org.cshaifa.spring.entities.responses.AddComplaintResponse;
import org.cshaifa.spring.entities.responses.GetComplaintsResponse;
import org.cshaifa.spring.entities.responses.GetOrdersResponse;
import org.cshaifa.spring.entities.responses.UpdateOrdersResponse;
import org.cshaifa.spring.utils.Constants;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CustomerProfileController {
    @FXML
    public Button refreshBtn;

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
    private Label added_complaint_text;

    private Customer customer;


    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Long> orderIdColumn;

    @FXML
    private TableColumn<Order, Double> orderSumColumn;

    @FXML
    private TableColumn<Order, Date> orderDateColumn;

    @FXML
    private TableColumn<Order, Date> supplyDateColumn;

    @FXML
    private TableColumn<Order, Boolean> isCompletedColumn;

//    @FXML
//    private TableColumn<Order, List<CatalogItem>> ordersItemsColumn;


    private List<Order> customerOrderList = new ArrayList<>();

    private List<Order> ordersList;

    private List<Complaint> customerComplaintList = new ArrayList<>();


    @FXML
    void refreshProfile(ActionEvent event) throws IOException {
        customerOrderList.clear();
        customerComplaintList.clear();
        initialize();
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
        if (App.getCurrentUser()!=null) {
            if(App.getCurrentUser() instanceof Customer && !complaintDescription.getText().isEmpty())
            {   customer = (Customer) App.getCurrentUser();
                Task<AddComplaintResponse> addComplaintTask = App.createTimedTask(() -> {
                    System.out.printf("customer is: ",  customer.getUsername());
                    System.out.printf("%d%n", customer.getId());
//                    Complaint complaint = new Complaint();
//                    complaint.setCustomer(customer);
//                    complaint.setComplaintOpen(true);
//                    complaint.setComplaintDescription(complaintDescription.getText().strip());
//                    complaint.setComplaintResponse("");
                    //customer.addComplaint(complaint);
                    //complaintList.getItems().add(complaint); //adding new complaint in UI
                    return ClientHandler.addComplaint(complaintDescription.getText().strip(), customer);
                }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

                addComplaintTask.setOnSucceeded(e2 -> {
                    AddComplaintResponse response2 = addComplaintTask.getValue();
                    complaintDescription.setText("");
                    if (!response2.isSuccessful()) {
                        // TODO: maybe log the specific exception somewhere
                        App.hideLoading();
                        System.err.println("Add complaint failed!");
                    }
                });

                addComplaintTask.setOnFailed(e2 -> {
                    // TODO: maybe properly log it somewhere
                    System.out.println("Add complaint failed!");
                    added_complaint_text.setText(Constants.UPDATED_COMPLAINT_FAILED);
                    added_complaint_text.setTextFill(Color.RED);
                    addComplaintTask.getException().printStackTrace();
                });

                new Thread(addComplaintTask).start();
                System.out.println("Add complaint Success!");
                added_complaint_text.setText(Constants.UPDATED_COMPLAINT);
                added_complaint_text.setTextFill(Color.GREEN);
            }
            else{
                invalid_customer_text.setText("failed to get customer ");
                invalid_customer_text.setTextFill(Color.RED);
            }
        }
    }


//    @FXML void cancelOrder(){
//        Order order = orderTable.getSelectionModel().getSelectedItem();
//    }

    private void addButtonToTable() {

        TableColumn<Order, Void> colBtn = new TableColumn("Cancel Order");
        if (!orderTable.getColumns().contains(colBtn)) {
            Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<TableColumn<Order, Void>, TableCell<Order, Void>>() {
                @Override
                public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
                    final TableCell<Order, Void> cell = new TableCell<Order, Void>() {

                        private final Button btn = new Button("Cancel");

                        {
                            btn.setOnAction((ActionEvent event) -> {
                                Order data = getTableView().getItems().get(getIndex());
                                System.out.println("selectedData: " + data);

                                Task<UpdateOrdersResponse> removeOrderTask = App.createTimedTask(() -> {
                                    return ClientHandler.updateOrders(data);
                                }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

                                removeOrderTask.setOnSucceeded(e -> {
                                    UpdateOrdersResponse response = removeOrderTask.getValue();
                                    ordersList.remove(data);//remove in UI locally
                                    customerOrderList.remove(data);

                                    //TODO: remove in UI
                                    if (!response.isSuccessful()) {
                                        // TODO: maybe log the specific exception somewhere
                                        App.hideLoading();
                                        System.err.println("Add complaint failed!");
                                    }
                                });

                                removeOrderTask.setOnFailed(e -> {
                                    // TODO: maybe properly log it somewhere
                                    System.out.println("Add complaint failed!");
//                                added_complaint_text.setText(Constants.UPDATED_COMPLAINT_FAILED);
//                                added_complaint_text.setTextFill(Color.RED);
                                    removeOrderTask.getException().printStackTrace();
                                });

                                new Thread(removeOrderTask).start();

                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };

            colBtn.setCellFactory(cellFactory);

            orderTable.getColumns().add(colBtn);
        }
    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser()!=null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        }
        else {
            welcomeText.setText("Welcome, unknown customer");
        }

        added_complaint_text.setText("");
        invalid_customer_text.setText("");

//        Button refreshButton = new Button("Refresh");
//        refreshButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                try {
//                    refreshProfile(mouseEvent);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
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
            if (App.getCurrentUser()!=null && App.getCurrentUser() instanceof Customer) {
                customer = (Customer) App.getCurrentUser();
            }
            else{
                invalid_customer_text.setText("failed to get customer ");
                invalid_customer_text.setTextFill(Color.RED);
            }
            List<Complaint> complaintList = response.getComplaintList();
            //List<Complaint> customerComplaintList = new ArrayList<>();
            for (Complaint complaint : complaintList) {
                if (complaint.getCustomer().getId() == customer.getId()) {
                    customerComplaintList.add(complaint);
                }
            }
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

            data.addAll(customerComplaintList);
            complaintTable.setItems(data);


            App.hideLoading();

    });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
            App.hideLoading();
        });
        new Thread(getComplaintsTask).start();

        //get orders table below:


        Task<GetOrdersResponse> getOrdersTask = App.createTimedTask(() -> {
            return ClientHandler.getOrders();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getOrdersTask.setOnSucceeded(e -> {
            if (getOrdersTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }
            GetOrdersResponse response = getOrdersTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting catalog failed");
                return;
            }
            orderTable.setEditable(true);
            if (App.getCurrentUser()!=null && App.getCurrentUser() instanceof Customer) {
                customer = (Customer) App.getCurrentUser();
            }
            else{
                invalid_customer_text.setText("failed to get customer ");
                invalid_customer_text.setTextFill(Color.RED);
            }
            ordersList = response.getOrdersList();
            //List<Order> orderList = response.getOrdersList();
            //List<Order> customerOrderList = new ArrayList<>();
            for (Order order :  ordersList) {
                if (order.getCustomer().getId() == customer.getId()) {
                    customerOrderList.add(order);
                }
            }
            ObservableList<Order> data = FXCollections.observableArrayList();

            orderIdColumn.setCellValueFactory(cellData ->
                    new SimpleLongProperty(cellData.getValue().getId()).asObject());

            orderSumColumn.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());

            orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

            supplyDateColumn.setCellValueFactory(new PropertyValueFactory<>("supplyDate"));

            isCompletedColumn.setCellValueFactory(cellData ->
                    new SimpleBooleanProperty(cellData.getValue().isCompleted()));

            data.addAll(customerOrderList);
            orderTable.setItems(data);

            addButtonToTable();

            App.hideLoading();

        });

        getOrdersTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getOrdersTask.getException().printStackTrace();
            App.hideLoading();
        });
        new Thread(getOrdersTask).start();

    }
}
