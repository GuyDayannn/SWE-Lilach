package org.cshaifa.spring.client;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.cshaifa.spring.entities.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.utils.Constants;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CustomerProfileController {
    @FXML
    private Text welcomeText;
    @FXML
    public Button refreshBtn;
    @FXML
    public Button signOutButton;
    @FXML
    public Button viewCatalogButton;
    @FXML
    private TextArea complaintDescription;
    @FXML
    private ComboBox<Long> storesComboBox;
    @FXML
    private Label invalid_customer_text;
    @FXML
    private Label added_complaint_text;
    @FXML
    private Label cancelOrderText;

    //Complaint Table
    @FXML
    private TableView<Complaint> complaintTable;
    @FXML
    private TableColumn<Complaint, Long> complaintIdColumn;
    @FXML
    private TableColumn<Complaint, String> complaintStatusColumn;
    @FXML
    private TableColumn<Complaint, String> complaintDescriptionColumn;
    @FXML
    private TableColumn<Complaint, String> complaintResponseColumn;
    @FXML
    private TableColumn<Complaint, Double> complaintCompensationAmountColumn;
    @FXML
    private TableColumn<Complaint, String> complaintStoreColumn;
    @FXML
    private TableColumn<Complaint, Date> complaintDateColumn;

    //Order Table
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Long> orderIdColumn;
    @FXML
    private TableColumn<Order, String> orderSumColumn;
    @FXML
    private TableColumn<Order, Date> orderDateColumn;
    @FXML
    private TableColumn<Order, Date> supplyDateColumn;
    @FXML
    private TableColumn<Order, String> isCompletedColumn;
    @FXML
    private TableColumn<Order, Void> colBtn = new TableColumn("Cancel Order");

    //Variables
    private Customer customer;
    private List<Order> customerOrderList = new ArrayList<>();
    private List<Complaint> customerComplaintList = new ArrayList<>();
    private List<Store> storeList;



    @FXML
    void refreshProfile(ActionEvent event)  {
        customerOrderList.clear();
        customerComplaintList.clear();
        orderTable.getColumns().remove(colBtn);
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
        if (customer != null && !complaintDescription.getText().isEmpty()) {
            int storeID = storesComboBox.getValue().intValue();
            Store store = storeList.get(storeID-1);
            Task<AddComplaintResponse> addComplaintTask = App.createTimedTask(() -> {
                System.out.printf("customer is: ", customer.getUsername());
                System.out.printf("%d%n", customer.getId());
                return ClientHandler.addComplaint(complaintDescription.getText().strip(), customer, store);
            }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

            addComplaintTask.setOnSucceeded(e2 -> {
                AddComplaintResponse response2 = addComplaintTask.getValue();
                complaintDescription.setText("");
                storesComboBox.valueProperty().setValue(null);
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
            try {
                Thread t = new Thread(addComplaintTask);
                t.start();
                t.join();
                customerComplaintList.clear();
                //complaintTable.getItems().clear();
                getComplaints(); //adding new complaint to UI
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            System.out.println("Add complaint Success!");
            added_complaint_text.setText(Constants.UPDATED_COMPLAINT);
            added_complaint_text.setTextFill(Color.GREEN);
        } else {
            invalid_customer_text.setText("failed to get customer ");
            invalid_customer_text.setTextFill(Color.RED);
        }
    }

    private void addButtonToTable() { //adding cancel order button

        Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<TableColumn<Order, Void>, TableCell<Order, Void>>() {
            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
                final TableCell<Order, Void> cell = new TableCell<Order, Void>() {

                    private final Button btn = new Button("Cancel");

                    {
                        btn.setOnAction((ActionEvent event) -> {

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel order?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                            alert.showAndWait();

                            if (alert.getResult() == ButtonType.YES) {
                                Order data = getTableView().getItems().get(getIndex());

                                Timestamp nowTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
                                if (nowTimestamp.getTime() - data.getSupplyDate().getTime() >= 3) {
                                    //return customer full amount
                                    //TODO
                                } else if (nowTimestamp.getTime() - data.getSupplyDate().getTime() >= 1) {
                                    //return customer 50% of order sum
                                } else {
                                    //not returning customer
                                }

                                Task<UpdateOrdersResponse> removeOrderTask = App.createTimedTask(() -> {
                                    return ClientHandler.updateOrders(data);
                                }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

                                removeOrderTask.setOnSucceeded(e -> {
                                    UpdateOrdersResponse response = removeOrderTask.getValue();
                                    //ordersList.remove(data);//remove in UI locally
                                    customerOrderList.remove(data);
                                    cancelOrderText.setText(Constants.CANCEL_ORDER);
                                    cancelOrderText.setTextFill(Color.GREEN);

                                    if (!response.isSuccessful()) {
                                        // TODO: maybe log the specific exception somewhere
                                        App.hideLoading();
                                        System.err.println("Cancel order failed!");
                                    }
                                });

                                removeOrderTask.setOnFailed(e -> {
                                    // TODO: maybe properly log it somewhere
                                    System.out.println("Cancel order failed!");
                                    added_complaint_text.setText(Constants.CANCEL_ORDER_FAILED);
                                    added_complaint_text.setTextFill(Color.RED);
                                    removeOrderTask.getException().printStackTrace();
                                });

                                try{
                                    Thread t = new Thread(removeOrderTask);
                                    t.start();
                                    t.join();
                                    customerOrderList.clear();
                                    getOrders(); //removing order from UI
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                }
                            }




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
        if (orderTable.getColumns().contains(colBtn)==false)
            orderTable.getColumns().add(colBtn);
    }

    public void getComplaints() {
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
            if (App.getCurrentUser() != null && App.getCurrentUser() instanceof Customer) {
                customer = (Customer) App.getCurrentUser();
            } else {
                invalid_customer_text.setText("failed to get customer ");
                invalid_customer_text.setTextFill(Color.RED);
            }
            List<Complaint> complaintList = response.getComplaintList();
            for (Complaint complaint : complaintList) {
                if (complaint.getCustomer().getId() == customer.getId()) {
                    customerComplaintList.add(complaint);
                }
            }
            ObservableList<Complaint> data = FXCollections.observableArrayList();

            complaintIdColumn.setText("Complaint ID");
            complaintIdColumn.setCellValueFactory(cellData ->
                    new SimpleLongProperty(cellData.getValue().getId()).asObject());

            complaintStatusColumn.setText("Status");
            complaintStatusColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getIsComplaintOpen()) {
                    return new SimpleStringProperty("Open");
                }
                return new SimpleStringProperty("Pending Response");
            });

            complaintDescriptionColumn.setText("Description");
            complaintDescriptionColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getComplaintDescription()));

            complaintResponseColumn.setText("Response");
            complaintResponseColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getComplaintResponse()));

            complaintCompensationAmountColumn.setText("Compensation");
            complaintCompensationAmountColumn.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getCompensationAmount()).asObject());

            complaintStoreColumn.setText("Store");
            complaintStoreColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(Long.toString(cellData.getValue().getStore().getId())));

            complaintDateColumn.setText("Date");
            complaintDateColumn.setCellValueFactory(new PropertyValueFactory<>("complaintTimestamp"));

            data.addAll(customerComplaintList);
            complaintTable.setItems(data);

            App.hideLoading();

        });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
            App.hideLoading();
        });
        try {
            Thread t = new Thread(getComplaintsTask);
            t.start();
            t.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public void getOrders() {
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
            List<Order> ordersList = response.getOrdersList();
            for (Order order :  ordersList) {
                if (order.getCustomer().getId() == customer.getId()) {
                    customerOrderList.add(order);
                }
            }
            ObservableList<Order> data = FXCollections.observableArrayList();

            orderIdColumn.setText("Order ID");
            orderIdColumn.setCellValueFactory(cellData ->
                    new SimpleLongProperty(cellData.getValue().getId()).asObject());

            orderSumColumn.setText("Total Price");
            orderSumColumn.setCellValueFactory(cellData -> {
                double totalPrice = cellData.getValue().getTotal();
                String totalPriceText = String.format("%,.2f", totalPrice);
                return new SimpleStringProperty(totalPriceText);
            });

            orderDateColumn.setText("Order Date");
            orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

            supplyDateColumn.setText("Supply Date");
            supplyDateColumn.setCellValueFactory(new PropertyValueFactory<>("supplyDate"));

            isCompletedColumn.setText("Completed");
            isCompletedColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().isCompleted() == true) {
                    return new SimpleStringProperty("Yes");
                }
                return new SimpleStringProperty("No");
            });

            data.addAll(customerOrderList);
            orderTable.setItems(data);

            addButtonToTable();

            orderTable.setRowFactory(tv -> {
                TableRow<Order> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY
                            && event.getClickCount() == 2) {
                        // TODO: Maybe add order details pop-up
                    }
                });
                return row ;
            });

            App.hideLoading();

        });

        getOrdersTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getOrdersTask.getException().printStackTrace();
            App.hideLoading();
        });

        try {
            Thread t = new Thread(getOrdersTask);
            t.start();
            t.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    @FXML
    public void initialize()  {
        if (App.getCurrentUser()!=null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        }
        else {
            welcomeText.setText("Welcome, unknown customer");
        }

        added_complaint_text.setText("");
        invalid_customer_text.setText("");

        if (App.getCurrentUser()!=null) {
            if (App.getCurrentUser() instanceof Customer)
                customer = (Customer) App.getCurrentUser();
        }

        getComplaints();

        getOrders();

        if(customer!=null) {
            storeList = customer.getStores();
            List<Long>  storesListID = new ArrayList<Long>();
            ObservableList<Long> data = FXCollections.observableArrayList();
            //showing customer only his complaints
            if (storeList.size()>=1) {
                for (int i = 0; i < storeList.size(); i++) {
                    Long id = (storeList.get(i).getId()-1);
                    if(id!=0.0){
                        storesListID.add(id);
                    }
                }
            }

            data.addAll(storesListID); //adding to dropdown combo
            storesComboBox.setItems(data);
        }
    }
}
