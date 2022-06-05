package org.cshaifa.spring.client;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.cshaifa.spring.entities.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.ImageUtils;


import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmployeeProfileController {

    // Top HBox
    @FXML
    private Text welcomeText;
    @FXML
    private Button catalogButton;
    @FXML
    private Button exitButton;


    // Accordion Objects
    @FXML
    private Accordion employeeControls;
    @FXML
    private TitledPane viewReportsPane;
    @FXML
    private TitledPane generateReportsPane;
    @FXML
    private TitledPane handleComplaintsPane;
    @FXML
    private TitledPane handleUsersPane;


    // Generate Store Reports Pane
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
    @FXML
    private CheckBox chainReport;
    @FXML
    private Text generateMessageText;


    // View Existing Reports Pane
    @FXML
    private Button addReportViewButton;
    @FXML
    private Button removeReportViewButton;
    @FXML
    private Button viewExistingReportsButton;
    @FXML
    private ComboBox<String> selectReport1CB;
    @FXML
    private ComboBox<String> selectReport2CB;
    @FXML
    private Text selectReport2CBText;


    // View/Handle Complaints  Pane
    @FXML
    private TextField complaintDescription;
    @FXML
    private TextField complaintResponse;
    @FXML
    private TextField complaintStatus;
    @FXML
    private Button closeComplaintButton;
    @FXML
    private TextField compensationAmount;
    @FXML
    private ComboBox<Long> complaintComboBox;
    @FXML
    private Label updated_complaint_text;


    // View/Handle Users Pane
    @FXML
    private Button editEmployeeBtn;
    @FXML
    private TextField customerAccountText;
    @FXML
    private TextField employeeStatusText;
    @FXML
    private ComboBox<String> customerComboBox;
    @FXML
    private ComboBox<String> customerStatusComboBox;
    @FXML
    private ComboBox<String> employeesTypeComboBox;
    @FXML
    private ComboBox<String> selectEmployeeComboBox;
    @FXML
    private ComboBox<String> selectStoreComboBox;
    @FXML
    private ComboBox<String> employeeStatusComboBox;
    @FXML
    private Label editResultLabel;


    // Variables
    private List<Complaint> complaintList= new ArrayList<>();
    private List<Store> storesList;
    private List<User> userList;
    private List<Customer> customerList = new ArrayList<>();
    private List<ChainEmployee> chainEmployeeList= new ArrayList<>();
    private List<CustomerServiceEmployee> customerServiceList= new ArrayList<>();
    private List<StoreManager> storeManagersList= new ArrayList<>();
    private List<Employee> employeeList= new ArrayList<>();
    private ChainManager chainManager;
    private SystemAdmin systemAdmin;
    private LocalDate reportStartDate;
    private LocalDate reportEndDate;
    private ReportType reportType;
    private Store reportStore;
    private Report report;
    private Customer selectedCustomer = new Customer();


    // Init lists from database
    void initEditEmployees() {
        employeesTypeComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            System.out.println(t);
            System.out.println(t1);
            if (t1!=null && t1.equals("Chain Employee")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (chainEmployeeList.size() >= 1) {
                    for (Employee employee : chainEmployeeList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                employeeStatusComboBox.setDisable(false);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Customer Service");
                employeeTypes.add("Store Manager");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }

            if (t1!=null && t1.equals("Customer Service")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (customerServiceList.size() >= 1) {
                    for (Employee employee : customerServiceList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                employeeStatusComboBox.setDisable(false);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Chain Employee");
                employeeTypes.add("Store Manager");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }

            if (t1!=null && t1.equals("Store Manager")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                if (storeManagersList.size() >= 1) {
                    for (Employee employee : storeManagersList) {
                        employeeNames.add(employee.getFullName());
                    }
                }
                employeeStatusComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Chain Employee");
                employeeTypes.add("Customer Service");
                employeeTypes.add("Chain Manager");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }

            if (t1!=null && t1.equals("Chain Manager")) {
                selectEmployeeComboBox.setDisable(false);
                selectEmployeeComboBox.getItems().clear();
                employeeStatusComboBox.getItems().clear();
                ObservableList<String> employeeNames = FXCollections.observableArrayList();
                employeeNames.add(chainManager.getFullName());
                selectEmployeeComboBox.getItems().addAll(employeeNames);
                employeeStatusComboBox.setDisable(false);
                ObservableList<String> employeeTypes = FXCollections.observableArrayList();
                employeeTypes.add("Store Manager");
                employeeTypes.add("Customer Service");
                employeeTypes.add("Customer Service");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }
        });

        employeeStatusComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            System.out.println(t);
            System.out.println(t1);
            if (t1!=null && (t1.equals("Store Manager") || t1.equals("Chain Employee"))) {
                System.out.println("chosen store manager / chain employee");
                selectStoreComboBox.setDisable(false);

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
                        selectStoreComboBox.getItems().add(store.getName());
                    }
                });

                getStoresTask.setOnFailed(e -> {
                    // TODO: maybe log somewhere else...
                    getStoresTask.getException().printStackTrace();
                });

                try {
                    Thread t2 = new Thread(getStoresTask);
                    t2.start();
                    t2.join();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();

                }

                //new Thread(getStoresTask).start();
            }
        });
    }

    void initUsers() {
        Task<GetUsersResponse> getUsersTask = App.createTimedTask(() -> {
            return ClientHandler.getUsers();
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        getUsersTask.setOnSucceeded(e -> {
            if (getUsersTask.getValue() == null) {
                App.hideLoading();
                System.err.println("Getting users failed");
                return;
            }
            GetUsersResponse response = getUsersTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                App.hideLoading();
                System.err.println("Getting users failed");
                return;
            }
            userList = response.getUsersList();
            //CustomerServiceEmployee customerServiceEmployee = new CustomerServiceEmployee();
            for(User user: userList){
                if(user.getClass().isAssignableFrom(ChainEmployee.class) ){
                    chainEmployeeList.add((ChainEmployee) user);
                    employeeList.add((Employee) user);
                    System.out.println("added chain employee");
                }
                else if(user.getClass().isAssignableFrom(StoreManager.class)){
                    storeManagersList.add((StoreManager) user);
                    employeeList.add((Employee) user);
                    System.out.println("added store manager");
                }
                else if(user.getClass().isAssignableFrom(CustomerServiceEmployee.class)){
                    customerServiceList.add((CustomerServiceEmployee) user);
                    employeeList.add((Employee) user);
                    System.out.println("added customer service employee");

                }
                else if(user.getClass().isAssignableFrom(Customer.class)){
                    customerList.add((Customer) user);
                    System.out.println("added customer");
                }
                else if(user.getClass().isAssignableFrom(ChainManager.class)){
                    chainManager = (ChainManager) user;
                    employeeList.add((Employee) user);
                    System.out.println("added chain manager");
                }
                else if(user.getClass().isAssignableFrom(SystemAdmin.class)){
                    systemAdmin = (SystemAdmin) user;
                    employeeList.add((Employee) user);
                    System.out.println("added system admin");
                }
                else{
                    System.out.println("Couldn't classify user");
                }
            }

            for (Customer customer : customerList) {
                //System.out.println(customer.getUsername());
                customerComboBox.getItems().add(customer.getUsername());
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
        //new Thread(getUsersTask).start();
    }

    void initComplaints() {
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
        });

        getComplaintsTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getComplaintsTask.getException().printStackTrace();
        });

        try {
            Thread t2 = new Thread(getComplaintsTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
//        new Thread(getComplaintsTask).start();

    }

    void initStores() {
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

        });

        getStoresTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            getStoresTask.getException().printStackTrace();
        });
        try {
            Thread t2 = new Thread(getStoresTask);
            t2.start();
            t2.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        //new Thread(getStoresTask).start();

    }


    // Create tasks
    public void createTaskCustomerUpdate(boolean toFreeze){
        Task<FreezeCustomerResponse> editCustomerTask = App.createTimedTask(() -> {
            return ClientHandler.freezeCustomer(selectedCustomer, toFreeze);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        editCustomerTask.setOnSucceeded(e -> {
            if (editCustomerTask.getValue() == null) {
                System.err.println("Updating customer failed");
                editResultLabel.setText(Constants.EDIT_CUSTOMER_FAILED);
                editResultLabel.setTextFill(Color.RED);
                return;
            }
            FreezeCustomerResponse response = editCustomerTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating customer failed");
                editResultLabel.setText(Constants.EDIT_CUSTOMER_FAILED);
                editResultLabel.setTextFill(Color.RED);
                return;
            }
            else{
                System.out.println("Successfully edited customer status");
                editResultLabel.setText(Constants.EDIT_CUSTOMER_SUCCESS);
                editResultLabel.setTextFill(Color.GREEN);
            }
        });

        editCustomerTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            editCustomerTask.getException().printStackTrace();
        });
        //new Thread(editCustomerTask).start();
        try {
            Thread t2 = new Thread(editCustomerTask);
            t2.start();
            t2.join();
            initUsers();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

    }

    public void createTaskEmployeeUpdate(ChainEmployee employee, Store store, String newType, String currType){
        System.out.println("inserted createTaskEmployeeUpdate");

        Task<EditEmployeeResponse> editEmployeeTask = App.createTimedTask(() -> {
            return ClientHandler.editEmployee(employee, store, newType, currType);
        }, Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS);

        editEmployeeTask.setOnSucceeded(e -> {
            if (editEmployeeTask.getValue() == null) {
                System.err.println("Updating employee failed");
                editResultLabel.setTextFill(Color.RED);
                editResultLabel.setText(Constants.EDIT_EMPLOYEE_FAILED);
                return;
            }
            EditEmployeeResponse response = editEmployeeTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating employee failed");
                editResultLabel.setTextFill(Color.RED);
                editResultLabel.setText(Constants.EDIT_EMPLOYEE_FAILED);
                return;
            }
            else{
                editResultLabel.setTextFill(Color.GREEN);
                editResultLabel.setText(Constants.EDIT_EMPLOYEE_SUCCESS);
                System.out.println("Updating employee succeeded");
            }
        });

        editEmployeeTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            editEmployeeTask.getException().printStackTrace();
        });

        //new Thread(editEmployeeTask).start();
        try {
            Thread t2 = new Thread(editEmployeeTask);
            t2.start();
            t2.join();
            initUsers();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private void messageDisappearanceTask(long millis, Label label) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(sleeperEvent -> {
            label.setVisible(false);
        });

        new Thread(sleeper).start();
    }

    // Top HBox Event Handlers
    @FXML
    void goCatalog(ActionEvent event) throws IOException {
        App.setWindowTitle("Edit Catalog");
        App.setContent("catalog");
    }

    @FXML
    void exitProfile(ActionEvent event) throws IOException {
        App.logoutUser();
        App.setWindowTitle("primary");
        App.setContent("primary");
    }


    // Generate Reports Handlers
    @FXML
    void selectStore(ActionEvent event) {
        String storeName = storeComboBox.getValue();
        for (Store store: storesList) {
            if(store.getName().equals(storeName)){
                reportStore = store;
                break;
            }
        }
    }

    @FXML
    void selectChain(ActionEvent event) {
        if (chainReport.isSelected()) {
            reportStore = null;
            storeComboBox.setDisable(true);
        }
        else {
            selectStore(event);
            storeComboBox.setDisable(false);
        }
    }

    @FXML
    void selectReportType(ActionEvent event) {
        String report = reportTypeComboBox.getValue();
        switch (report) {
            case "Orders" -> reportType = ReportType.ORDERS;
            case "Revenue" -> reportType = ReportType.REVENUE;
            case "Complaints" -> reportType = ReportType.COMPLAINTS;
        }
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
        if (reportType!=null && reportStartDate!=null && reportEndDate!=null) {
            report = new Report(reportType, reportStore, reportStartDate, reportEndDate);

            boolean success = false;
            if (chainReport.isSelected()) {
                success = report.generateChainHistogram(storesList);
            }
            else {
                success = report.generateHistogram();
            }
            if (success) {
                viewReportButton.setDisable(false);
                generateMessageText.setFill(Color.GREEN);
                generateMessageText.setText(Constants.GENERATE_REPORT_SUCCESS);
                System.out.println("Report generated successfully.");
            }
            else {
                generateMessageText.setFill(Color.RED);
                generateMessageText.setText(Constants.GENERATE_REPORT_FAILED);
                System.out.println("Generating report failed.");
            }

        }
        else {
            generateMessageText.setFill(Color.RED);
            generateMessageText.setText(Constants.MISSING_REQUIREMENTS);
            System.out.println("Insert required data.");
        }

    }

    @FXML
    void viewReport(ActionEvent event) {
        String path = report.getReportPath();
        App.setCurrentReportDisplayed(report);
        App.popUpLaunch(viewReportButton, "ReportPopUp");
    }


    // View Existing Reports Handlers
    @FXML
    void viewExistingReports(ActionEvent event) {
        // TODO: Get report images
        App.popUpLaunch(viewExistingReportsButton, "TwoReportsPopUp");
    }


    // Handle Complaints Handlers
    @FXML
    void openComplaint(ActionEvent event) {

        long complaintID  = complaintComboBox.getValue(); //getting selected complaint ID
        Complaint selectedComplaint = null;
        for (Complaint complaint: complaintList) {
            if (complaint.getId()==complaintID) {
                selectedComplaint = complaint;
                if (selectedComplaint.getIsComplaintOpen()) {
                    complaintStatus.getStyleClass().add("open-complaint");
                    closeComplaintButton.setDisable(false);
                }
                else {
                    complaintStatus.getStyleClass().add("closed-complaint");
                    closeComplaintButton.setDisable(true);
                }
            }
        }
        if(selectedComplaint!=null){
            String desc ="";
            if(selectedComplaint.getComplaintDescription() != null) {
                complaintDescription.setText(selectedComplaint.getComplaintDescription());
            }
            String complaintStatusStr = "";
            if (selectedComplaint.getIsComplaintOpen()) {
                complaintStatusStr = "Open";
            } else {
                complaintStatusStr = "Closed";
            }
            complaintStatus.setText(complaintStatusStr);
            compensationAmount.setText(Double.toString(selectedComplaint.getCompensationAmount()));
            if(selectedComplaint.getComplaintResponse()!=null){
                complaintResponse.setText(selectedComplaint.getComplaintResponse());
            }
        } else {
            System.out.println("complaint is null and id: ");
        }
    }


    @FXML
    void closeComplaint(ActionEvent event) {
        long complaintID  = complaintComboBox.getValue(); //getting selected complaint ID
        Complaint updatedComplaint = null;
        for (Complaint complaint: complaintList) {
            if (complaint.getId()==complaintID) {
                updatedComplaint = complaint;
            }
        }
        if (updatedComplaint!=null) {
            updatedComplaint.setComplaintResponse(complaintResponse.getText());
            updatedComplaint.setComplaintOpen(false);
            double compensationNum  = Double.parseDouble(compensationAmount.getText());
            updatedComplaint.setCompensationAmount(compensationNum);

            complaintComboBox.valueProperty().setValue(null);
            complaintResponse.setText("");
            compensationAmount.setText("");
            complaintStatus.setText("");
            complaintDescription.setText("");

            Complaint finalUpdatedComplaint = updatedComplaint;
            Task<UpdateComplaintResponse> updateComplaintTask = App.createTimedTask(() -> {
                return ClientHandler.updateComplaint(finalUpdatedComplaint);
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
            //new Thread(updateComplaintTask).start();
            try {
                Thread t2 = new Thread(updateComplaintTask);
                t2.start();
                t2.join();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

    }

    // View/Handle Users
    @FXML
    public void selectCustomer(ActionEvent event) {
        customerAccountText.clear();
        customerStatusComboBox.getItems().clear();
        String custUsername = customerComboBox.getSelectionModel().getSelectedItem();
        //Customer selectedCustomer = new Customer();
        for(Customer customer: customerList){
            if(customer.getUsername().equals(custUsername)){
                selectedCustomer = customer;
                break;
            }
        }
        boolean isFrozen = selectedCustomer.isFrozen();
        List<String> changeAccountStatus = new ArrayList<String>();
        ObservableList<String> data = FXCollections.observableArrayList();
        if(isFrozen){
            customerAccountText.setText("Frozen");
            changeAccountStatus.add("Active");
            data.addAll(changeAccountStatus);
            customerStatusComboBox.setItems(data);
        }

        else{
            changeAccountStatus.add("Frozen");
            data.addAll(changeAccountStatus);
            customerStatusComboBox.setItems(data);
            customerAccountText.setText("Active");
        }
    }

    @FXML
    public void editCustomerStatus(ActionEvent event) {
        String selectedStatus = customerStatusComboBox.getValue();
        boolean createTask = false;
        if(selectedStatus.equals("Active") && selectedCustomer.isFrozen()){
            //selectedCustomer.freeze();
            createTaskCustomerUpdate(false);
        }
        if(selectedStatus.equals("Frozen") && !selectedCustomer.isFrozen()){
            //selectedCustomer.unfreeze();
            createTaskCustomerUpdate(true);
        }

        customerComboBox.valueProperty().setValue(null); //edit to clear all
        customerStatusComboBox.valueProperty().setValue(null);
        customerAccountText.clear();
    }

    @FXML
    public void selectAccountStatus(ActionEvent event) {
    }

    @FXML
    public void selectEmployee(ActionEvent event) {

    }

    @FXML
    public void editEmployeeStatus(ActionEvent event) {

    }

    @FXML
    public void editEmployee(ActionEvent event) {
        System.out.println("inserted into editEmployee");
        String selectedStatus = employeeStatusComboBox.getValue();
        String employeeName = selectEmployeeComboBox.getValue();
        Store selectedStore= null;
        if(selectStoreComboBox.getValue()!=null){
            selectStoreComboBox.getItems().clear();
            String storeName = selectStoreComboBox.getValue();
            for(Store store : storesList){
                if(storeName.equals(store.getName())){
                    selectedStore = store;
                    break;
                }
            }
        }
        if(employeesTypeComboBox.getValue().equals("Store Manager")){
            System.out.println("inserted into editEmployee store manager");
            StoreManager selectedManager = null;
            for(StoreManager manager: storeManagersList) {
                if (manager.getFullName().equals(employeeName)) {
                    selectedManager = manager;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedManager, selectedStore, selectedStatus, "Store Manager");
        }
        else if(employeesTypeComboBox.getValue().equals("Chain Employee")){
            System.out.println("inserted into editEmployee chain employee");
            ChainEmployee selectedEmployee = null;
            for (ChainEmployee employee: chainEmployeeList) {
                if (employee.getFullName().equals(employeeName)) {
                    selectedEmployee = employee;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedEmployee, selectedStore, selectedStatus, "Chain Employee");
        }
        else if (employeesTypeComboBox.getValue().equals("Customer Service")) {
            CustomerServiceEmployee selectedEmployee = null;
            for(CustomerServiceEmployee employee: customerServiceList) {
                if (employee.getFullName().equals(employeeName)) {
                    selectedEmployee = employee;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedEmployee, null, selectedStatus, "Customer Service");
        }

        else{//we're editing chain maneger
            createTaskEmployeeUpdate(chainManager, null, selectedStatus, "Chain Manager");
        }
        employeesTypeComboBox.valueProperty().setValue(null); //edit to clear all
        selectStoreComboBox.valueProperty().setValue(null);
        employeeStatusComboBox.valueProperty().setValue(null);
        selectEmployeeComboBox.valueProperty().setValue(null);
    }

//    @FXML
//    public void selectEmployeeType(ActionEvent event) {
//
//    }
//
//    @FXML
//    public void editManagerStore(ActionEvent event) {
//
//    }




    @FXML
    public void initialize() {

        if (App.getCurrentUser() != null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        } else {
            welcomeText.setText("Welcome, unknown employee");
        }

        addReportViewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectReport2CB.setVisible(true);
                selectReport2CBText.setVisible(true);
                removeReportViewButton.setVisible(true);
                addReportViewButton.setVisible(false);
            }
        });
        removeReportViewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectReport2CB.setVisible(false);
                selectReport2CBText.setVisible(false);
                removeReportViewButton.setVisible(false);
                addReportViewButton.setVisible(true);
            }
        });

        //TODO: edit to show only to authorized users
        if (App.getCurrentUser() != null) {
            /*
            if(App.getCurrentUser().getClass() == ChainEmployee.class){
                paneStoreReport.setVisible(false);
                paneChainReport.setVisible(false);
                handleUsersPane.setVisible(false);
                viewTwoReportsPane.setVisible(false);
            }*/
            if (App.getCurrentUser().getClass() == StoreManager.class) {
                chainReport.setVisible(false);
                employeeControls.getPanes().remove(handleUsersPane);
            } else if (App.getCurrentUser().getClass() == CustomerServiceEmployee.class) {
                employeeControls.getPanes().removeAll(generateReportsPane, viewReportsPane, handleUsersPane);
            } else if (App.getCurrentUser().getClass() == ChainManager.class) {
                employeeControls.getPanes().remove(handleUsersPane);
            } else if (App.getCurrentUser().getClass() == SystemAdmin.class) {
                employeeControls.getPanes().remove(handleComplaintsPane);
            }
        }

        //calling db server tasks to get data
        initComplaints();
        initStores();
        initUsers();
        initEditEmployees();

    }
}