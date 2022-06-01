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
import org.cshaifa.spring.entities.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.utils.Constants;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private ComboBox<Long> storesComboBox;

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
    private TableColumn<Complaint, Long> complaintStoreColumn;

    @FXML
    private TableColumn<Complaint, Date> complaintDateColumn;

    @FXML
    private TableColumn<Order, Void> colBtn = new TableColumn("Cancel Order");

    @FXML
    private Label invalid_customer_text;

    @FXML
    private Label added_complaint_text;

    @FXML
    private Label cancelOrderText;

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

    private List<Order> customerOrderList = new ArrayList<>();
    private List<Order> ordersList;
    private List<Complaint> customerComplaintList = new ArrayList<>();
    private List<Store> storeList;
    //private List<Store> customerStoresList = new ArrayList<>();


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
                complaintTable.getItems().clear();
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
                                ordersList.remove(data);//remove in UI locally
                                customerOrderList.remove(data);
                                cancelOrderText.setText(Constants.CANCEL_ORDER);
                                cancelOrderText.setTextFill(Color.GREEN);

                                //TODO: remove in UI
                                if (!response.isSuccessful()) {
                                    // TODO: maybe log the specific exception somewhere
                                    App.hideLoading();
                                    System.err.println("Cancel oeder failed!");
                                }
                            });

                            removeOrderTask.setOnFailed(e -> {
                                // TODO: maybe properly log it somewhere
                                System.out.println("Cancel oeder failed!");
                                added_complaint_text.setText(Constants.CANCEL_ORDER_FAILED);
                                added_complaint_text.setTextFill(Color.RED);
                                removeOrderTask.getException().printStackTrace();
                            });

                            try{
                                Thread t = new Thread(removeOrderTask);
                                t.start();
                                t.join();
                                customerOrderList.clear();
                                complaintTable.getItems().clear();
                                getOrders(); //removing order from UI
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
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

            complaintStoreColumn.setCellValueFactory(cellData ->
                    new SimpleLongProperty(cellData.getValue().getStore().getId()).asObject());

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
            ordersList = response.getOrdersList();
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

        if(customer!=null){
            storeList = customer.getStores();
            List<Long>  storesListID = new ArrayList<Long>();
            ObservableList<Long> data = FXCollections.observableArrayList();
            //showing customer only his complaints
            if(storeList.size()>=1){
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

    public void selectStore(ActionEvent event) {
        if(storesComboBox.getValue()!= null){
            long storeID  = storesComboBox.getValue(); //getting selected complaint ID

            Store store = storeList.get((int) storeID);
        }

    }
}
