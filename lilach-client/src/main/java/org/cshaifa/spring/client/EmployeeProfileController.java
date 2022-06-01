package org.cshaifa.spring.client;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.cshaifa.spring.entities.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.utils.Constants;


import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmployeeProfileController {
    //FXML objects
    @FXML
    private TextField customerAccountText;
    @FXML
    private ComboBox customerComboBox;
    @FXML
    private ComboBox employeesComboBox;
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
    private ComboBox<Long> complaintComboBox;
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
    private Label updated_complaint_text;
    @FXML
    private TitledPane paneStoreReport;
    @FXML
    private TitledPane paneChainReport;
    @FXML
    private TitledPane handleUsersPane;
    @FXML
    private ComboBox<String> storeComboBox;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button viewReportButton;

    // Variables
    private List<Complaint> complaintList;
    private List<Store> storesList;
    private List<User> userList;
    private List<Customer> customerList = new ArrayList<>();
    private List<Employee> employeeList= new ArrayList<>();

    private LocalDate reportStartDate;
    private LocalDate reportEndDate;
    private ReportType reportType;
    private Store reportStore;
    private Report report;



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
    void openComplaint(ActionEvent event) { //on view complaint click
        //first clearning screens
        complaintdescription.clear();
        complaintStatus.clear();
        compensationamount.clear();
        complaintresponse.clear();

        long complaintID  = complaintComboBox.getValue(); //getting selected complaint ID
        Complaint complaint = complaintList.get((int) complaintID);

        complaintdescription.setText(complaint.getComplaintDescription());
        String complaintStatusStr = "";
        if (complaint.getIsComplaintOpen()) {
            complaintStatusStr = "Open";
        } else {
            complaintStatusStr = "Closed";
        }

        complaintStatus.setText(complaintStatusStr);
        compensationamount.setText((Double.toString(complaint.getCompensationAmount())));
        complaintresponse.setText((complaint.getComplaintResponse()));
    }

    @FXML
    void closeComplaint(ActionEvent event) { //TODO: fix it closes the correct complaint
        long complaintID  = complaintComboBox.getValue(); //getting selected complaint ID
        Complaint updatedComplaint = complaintList.get((int) complaintID);
        updatedComplaint.setComplaintResponse(complaintresponse.getText());
        updatedComplaint.setComplaintOpen(false);
        double compensationNum  = Double.parseDouble(compensationamount.getText());
        updatedComplaint.setCompensationAmount(compensationNum);

        complaintComboBox.valueProperty().setValue(null);
        complaintresponse.setText("");
        compensationamount.setText("");
        complaintStatus.setText("");
        complaintdescription.setText("");

        Task<UpdateComplaintResponse> updateComplaintTask = App.createTimedTask(() -> {
            return ClientHandler.updateComplaint(updatedComplaint);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        updateComplaintTask.setOnSucceeded(e -> {
            UpdateComplaintResponse response = updateComplaintTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating Complaint failed");
                updated_complaint_text.setText("Failed to close complaint");
                updated_complaint_text.setTextFill(Color.RED);
                return;
            }
            updated_complaint_text.setText("You have successfully closed the complaint");
            updated_complaint_text.setTextFill(Color.GREEN);
        });

        updateComplaintTask.setOnFailed(e -> {
            // TODO: maybe properly log it somewhere
            updateComplaintTask.getException().printStackTrace();
            updated_complaint_text.setText("Failed to close complaint");
            updated_complaint_text.setTextFill(Color.RED);
        });
        new Thread(updateComplaintTask).start();
    }

    @FXML
    void exitProfile(ActionEvent event) throws IOException {
        App.logoutUser();
        App.setWindowTitle("primary");
        App.setContent("primary");
    }

    @FXML
    void goCatalog(ActionEvent event) throws IOException {
        App.setWindowTitle("Edit Catalog");
        App.setContent("catalog");
    }

    @FXML
    void selectStore(ActionEvent event) {
        String storeName = storeComboBox.getValue();
        for(Store store: storesList){
            if(store.getName().equals(storeName)){
                reportStore = store;
                break;
            }
        }
    }

    @FXML
    void selectReportType(ActionEvent event) {
        String report = reportTypeComboBox.getValue();
        if(report.equals("Orders"))
            reportType = ReportType.ORDERS;
        else if (report.equals("Revenue"))
            reportType = ReportType.REVENUE;
        else if(report.equals("Complaints"))
            reportType = ReportType.COMPLAINTS;
    }

    @FXML
    void setStartDate(ActionEvent event) {
        reportStartDate = startDatePicker.getValue();
    }

    @FXML
    void setEndDate(ActionEvent event) {
        reportEndDate = endDatePicker.getValue();
    }

    @FXML
    void generateReport(ActionEvent event) {
        report = new Report(reportType, reportStore, reportStartDate, reportEndDate);
        report.generateHistogram();
        viewReportButton.setDisable(false);

    }

    @FXML
    void viewReport(ActionEvent event) {
        String path = report.getReportPath();
        App.setCurrentReportDisplayed(report);
        App.popUpLaunch(viewReportButton, "ReportPopUp");
    }

    void initComplaints(){
        //initialize complaints below:
        Task<GetComplaintsResponse> getComplaintsTask = App.createTimedTask(() -> {
            return ClientHandler.getComplaints();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getComplaintsTask.setOnSucceeded(e -> {
            if (getComplaintsTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting complaints failed");
                return;
            }
            GetComplaintsResponse response = getComplaintsTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting complaints failed");
                return;
            }
            complaintList = response.getComplaintList();

            List<Long> complaintListID = new ArrayList<Long>();
            ObservableList<Long> data = FXCollections.observableArrayList();
            //showing customer only his complaints
            if (complaintList.size() >= 1) {
                for (int i = 0; i < complaintList.size()-1; i++) {
                    Long id = (complaintList.get(i).getId()); //id 1 shows complaint 2
                    if(id!=0.0) {
                        complaintListID.add(id);
                    } //TODO: fix index id jumps in 1
                }
            }

            data.addAll(complaintListID); //adding to dropdown combo
            complaintComboBox.setItems(data);
            //new Thread(getComplaintsTask).start();

        });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
        });

        try {
//            Thread t1 = new Thread(getComplaintsTask);
//            t1.start();
//            t1.join();
            Thread t2 = new Thread(getComplaintsTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }


    void initStores(){
        // initialize stores below
        Task<GetStoresResponse> getStoresTask = App.createTimedTask(() -> {
            return ClientHandler.getStores();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getStoresTask.setOnSucceeded(e -> {
            if (getStoresTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting stores failed");
                return;
            }
            GetStoresResponse response = getStoresTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting stores failed");
                return;
            }
            storesList = response.getStores();

            for (Store store : storesList) {
                //System.out.println(store.getName());
                storeComboBox.getItems().add(store.getName());
            }

            //new Thread(getStoresTask).start();
        });

        getStoresTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getStoresTask.getException().printStackTrace();
        });
        try {
//            Thread t1 = new Thread(getComplaintsTask);
//            t1.start();
//            t1.join();
            Thread t2 = new Thread(getStoresTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }

    void initUsers(){
        // initialize stores below

        Task<GetUsersResponse> getUsersTask = App.createTimedTask(() -> {
            return ClientHandler.getUsers();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getUsersTask.setOnSucceeded(e -> {
            if (getUsersTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting stores failed");
                return;
            }
            GetUsersResponse response = getUsersTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting stores failed");
                return;
            }
            userList = response.getUsersList();
            ChainEmployee chainEmployee = new ChainEmployee();
            StoreManager storeManager = new StoreManager();
            SystemAdmin systemAdmin = new SystemAdmin();
            CustomerServiceEmployee customerServiceEmployee = new CustomerServiceEmployee();
            for(User user: userList){
//                        == ChainEmployee.class || user.getClass() ==CustomerServiceEmployee.class
//                 || user.getClass() ==SystemAdmin.class)
                if(user.getClass().isAssignableFrom(Employee.class)){
                    employeeList.add((Employee) user);
                    System.out.println("added employee");

                }
                if(user.getClass().isAssignableFrom(Customer.class)){
                    customerList.add((Customer) user);
                    System.out.println("added customer");
                }
                else{
                    System.out.println("Couldn't classify user");
                }
            }

            for (Customer customer : customerList) {
                System.out.println(customer.getUsername());
                customerComboBox.getItems().add(customer.getUsername());
            }

            for (Employee employee : employeeList) {
                System.out.println(employee.getUsername());
                employeesComboBox.getItems().add(employee.getUsername());
            }
        });

        getUsersTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getUsersTask.getException().printStackTrace();
        });
        try {
            Thread t2 = new Thread(getUsersTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }

    @FXML
    public void initialize() {
        if (App.getCurrentUser() != null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        } else {
            welcomeText.setText("Welcome, unknown employee");
        }
        //TODO: get all users request
        //TODO: edit so hide edit catalog for customer service employee
        ChainEmployee chainEmployee = new ChainEmployee();
        StoreManager storeManager = new StoreManager();
        SystemAdmin systemAdmin = new SystemAdmin();
        if (App.getCurrentUser() != null) {
            /*
            if(App.getCurrentUser().getClass() == chainEmployee.getClass()){
                paneStoreReport.setVisible(false);
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
            }*/
            if (App.getCurrentUser().getClass() == storeManager.getClass()) {
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
            } else if (App.getCurrentUser().getClass() == systemAdmin.getClass()) {
                //else it's system admin and he can see all options
            }
        }
        //calling db server tasks to get data
        initComplaints();
        initStores();
        initUsers();
    }
    public void viewAllComplaints(ActionEvent event) {

    }

    //methods for handle users below
    public void selectCustomer(ActionEvent event) {
//        String custUsername = customerComboBox.getItems().
//        Customer selectedCustomer;
//        for(Customer customer: customerList){
//            if(customer.getUsername().equals(custUsername)){
//                selectedCustomer = customer;
//                break;
//            }
//        }
//        boolean isFrozen = selectedCustomer.isFrozen();
//        if(isFrozen)
//            customerAccountText.setText("Frozen");
//        else
//            customerAccountText.setText("Active");
    }

    public void editCustomerStatus(ActionEvent event) {
    }

    public void selectAccpuntStatus(ActionEvent event) {
    }

    public void selectEmployee(ActionEvent event) {
    }

    public void selectEmployeeStatus(ActionEvent event) {
    }

    public void editEmployee(ActionEvent event) {
    }
}