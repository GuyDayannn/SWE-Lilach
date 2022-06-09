package org.cshaifa.spring.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cshaifa.spring.entities.ChainEmployee;
import org.cshaifa.spring.entities.ChainManager;
import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.CustomerServiceEmployee;
import org.cshaifa.spring.entities.Employee;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.StoreManager;
import org.cshaifa.spring.entities.SystemAdmin;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.responses.EditEmployeeResponse;
import org.cshaifa.spring.entities.responses.FreezeCustomerResponse;
import org.cshaifa.spring.entities.responses.GetComplaintsResponse;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.entities.responses.GetUsersResponse;
import org.cshaifa.spring.entities.responses.UpdateComplaintResponse;
import org.cshaifa.spring.utils.Constants;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

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
    private TitledPane generateReportsPane;
    @FXML
    private TitledPane handleComplaintsPane;
    @FXML
    private TitledPane handleUsersPane;


    // View Reports Pane
    @FXML
    private VBox reportsVBox;
    @FXML
    private VBox report1Vbox;
    @FXML
    private VBox report2Vbox;
    @FXML
    private ComboBox<String> storeComboBox;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button generateReportButton;
    @FXML
    private CheckBox chainReport;
    @FXML
    private ComboBox<String> storeComboBox1;
    @FXML
    private ComboBox<String> reportTypeComboBox1;
    @FXML
    private DatePicker startDatePicker1;
    @FXML
    private DatePicker endDatePicker1;
    @FXML
    private CheckBox chainReport1;
    @FXML
    private Label generateMessageText;
    @FXML
    private Button addReportViewButton;
    @FXML
    private HBox addReportViewButtonBox;
    @FXML
    private Button removeReportViewButton;


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

    // Report1
    private LocalDate reportStartDate;
    private LocalDate reportEndDate;
    private ReportType reportType;
    private Store reportStore;
    private Report report = null;

    //Report2
    private LocalDate reportStartDate1;
    private LocalDate reportEndDate1;
    private ReportType reportType1;
    private Store reportStore1;
    private Report report1 = null;

    private boolean two_reports = false;
    private Customer selectedCustomer = null;


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
                selectEmployeeComboBox.getItems().clear();
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
                employeeTypes.add("Chain Employee");
                employeeStatusComboBox.getItems().addAll(employeeTypes);
            }
        });

        employeeStatusComboBox.valueProperty().addListener((ChangeListener<String>) (ov, t, t1) -> {
            System.out.println(t);
            System.out.println(t1);
            if (t1!=null && (t1.equals("Store Manager") || t1.equals("Chain Employee"))) {
                System.out.println("chosen store manager / chain employee");
                selectStoreComboBox.setDisable(false);
                if(selectStoreComboBox.getItems().isEmpty()==true) {
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
                }
                //new Thread(getStoresTask).start();
            }

            if (t1!=null && (t1.equals("Customer Service") || t1.equals("Chain Manager"))) {
                System.out.println("chosen Customer service / chain manager");
                selectStoreComboBox.setDisable(true);
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
            selectEmployeeComboBox.getItems().clear();
            employeeList.clear();
            chainEmployeeList.clear();
            storeManagersList.clear();
            customerServiceList.clear();

            for(User user: userList){
                if(user.getClass().isAssignableFrom(ChainEmployee.class) ){
                    chainEmployeeList.add((ChainEmployee) user);
                    employeeList.add((Employee) user);
                    //System.out.println("added chain employee");
                }
                else if(user.getClass().isAssignableFrom(StoreManager.class)) {
                    if (user.getClass()!=(ChainManager.class)) {
                        storeManagersList.add((StoreManager) user);
                        employeeList.add((Employee) user);
                        //System.out.println("added store manager");
                    }
                }
                else if(user.getClass().isAssignableFrom(CustomerServiceEmployee.class)){
                    customerServiceList.add((CustomerServiceEmployee) user);
                    employeeList.add((Employee) user);
                    //System.out.println("added customer service employee");

                }
                else if(user.getClass().isAssignableFrom(Customer.class)){
                    customerList.add((Customer) user);
                    //System.out.println("added customer");
                }
                else if(user.getClass().isAssignableFrom(ChainManager.class)){
                    chainManager = (ChainManager) user;
                    employeeList.add((Employee) user);
                    //System.out.println("added chain manager");
                }
                else if(user.getClass().isAssignableFrom(SystemAdmin.class)){
                    systemAdmin = (SystemAdmin) user;
                    employeeList.add((Employee) user);
                    //System.out.println("added system admin");
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
            if (complaintList != null) {
                for (int i = 0; i < complaintList.size(); i++) {
                    Long id = (complaintList.get(i).getId());
                    if(id!=0.0) {
                        complaintListID.add(id);
                    }
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

            if (storesList!=null) {
                storesList.clear();
            }
            storesList = response.getStores();

            for (Store store : storesList) {
                //System.out.println(store.getName());
                storeComboBox.getItems().add(store.getName());
                storeComboBox1.getItems().add(store.getName());
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
                editResultLabel.setVisible(true);
                editResultLabel.setText(Constants.EDIT_CUSTOMER_FAILED);
                editResultLabel.setTextFill(Color.RED);
                messageDisappearanceTask(4000, editResultLabel);
                return;
            }
            FreezeCustomerResponse response = editCustomerTask.getValue();
            if (!response.isSuccessful()) {
                // TODO: maybe log the specific exception somewhere
                System.err.println("Updating customer failed");
                editResultLabel.setVisible(true);
                editResultLabel.setText(Constants.EDIT_CUSTOMER_FAILED);
                editResultLabel.setTextFill(Color.RED);
                messageDisappearanceTask(4000, editResultLabel);
                return;
            }
            else{
                System.out.println("Successfully edited customer status");
                editResultLabel.setVisible(true);
                editResultLabel.setText(Constants.EDIT_CUSTOMER_SUCCESS);
                editResultLabel.setTextFill(Color.GREEN);
                messageDisappearanceTask(4000, editResultLabel);
            }
        });

        editCustomerTask.setOnFailed(e -> {
            // TODO: maybe log somewhere else...
            System.err.println("Updating customer failed");
            editResultLabel.setVisible(true);
            editResultLabel.setText(Constants.EDIT_CUSTOMER_FAILED);
            editResultLabel.setTextFill(Color.RED);
            messageDisappearanceTask(4000, editResultLabel);
            editCustomerTask.getException().printStackTrace();
        });
        //new Thread(editCustomerTask).start();
        try {
            Thread t2 = new Thread(editCustomerTask);
            t2.start();
            t2.join();
            customerList.clear();
            initUsers();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

    }

    public void createTaskEmployeeUpdate(ChainEmployee employee, Store store, Store oldStore, String newType, String currType){
        System.out.println("inserted createTaskEmployeeUpdate");

        Task<EditEmployeeResponse> editEmployeeTask = App.createTimedTask(() -> {
            return ClientHandler.editEmployee(employee, store, oldStore, newType, currType);
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
            initEditEmployees();
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


    // View Reports Handlers
    @FXML
    void selectStore(ActionEvent event) {
        String storeName = storeComboBox.getValue();
        for (Store store: storesList) {
            if (store.getName().equals(storeName)){
                reportStore = store;
                System.out.println(store.getName());
                break;
            }
        }
    }

    @FXML
    void selectStore1(ActionEvent event) {
        String storeName = storeComboBox1.getValue();
        for (Store store: storesList) {
            if(store.getName().equals(storeName)){
                reportStore1 = store;
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


        if (chainReport1.isSelected()) {
            reportStore1 = null;
            storeComboBox1.setDisable(true);
        }
        else {
            selectStore1(event);
            storeComboBox1.setDisable(false);
        }
    }

    @FXML
    void selectReportType(ActionEvent event) {
        if (event.getSource() == reportTypeComboBox) {
            String report = reportTypeComboBox.getValue();
            switch (report) {
                case "Orders" -> reportType = ReportType.ORDERS;
                case "Revenue" -> reportType = ReportType.REVENUE;
                case "Complaints" -> reportType = ReportType.COMPLAINTS;
            }
        }
        else {
            String report = reportTypeComboBox1.getValue();
            switch (report) {
                case "Orders" -> reportType1 = ReportType.ORDERS;
                case "Revenue" -> reportType1 = ReportType.REVENUE;
                case "Complaints" -> reportType1 = ReportType.COMPLAINTS;
            }
        }
    }

    @FXML
    void setStartDate(ActionEvent event) {
        if (event.getSource() == startDatePicker) {
            reportStartDate = startDatePicker.getValue();
        }
        else {
            reportStartDate1 = startDatePicker1.getValue();
        }
    }

    @FXML
    void setEndDate(ActionEvent event) {
        if (event.getSource() == endDatePicker) {
            reportEndDate = endDatePicker.getValue();
        }
        else {
            reportEndDate1 = endDatePicker1.getValue();
        }
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
                generateMessageText.setVisible(true);
                generateMessageText.setTextFill(Color.GREEN);
                generateMessageText.setText(Constants.GENERATE_REPORT_SUCCESS);
                messageDisappearanceTask(4000, generateMessageText);
                System.out.println("Report generated successfully.");
            }
            else {
                generateMessageText.setVisible(true);
                generateMessageText.setTextFill(Color.RED);
                generateMessageText.setText(Constants.GENERATE_REPORT_FAILED);
                messageDisappearanceTask(4000, generateMessageText);
                System.out.println("Generating report failed.");
                return;
            }
            App.setCurrentReportDisplayed(report);
        }
        else {
            generateMessageText.setVisible(true);
            generateMessageText.setTextFill(Color.RED);
            generateMessageText.setText(Constants.MISSING_REQUIREMENTS);
            messageDisappearanceTask(4000, generateMessageText);
            System.out.println("Insert required data.");
            return;
        }

        if (two_reports) {
            if (reportType1!=null && reportStartDate1!=null && reportEndDate1!=null) {
                report1 = new Report(reportType1, reportStore1, reportStartDate1, reportEndDate1);

                boolean success = false;
                if (chainReport1.isSelected()) {
                    success = report1.generateChainHistogram(storesList);
                }
                else {
                    success = report1.generateHistogram();
                }
                if (success) {
                    generateMessageText.setVisible(true);
                    generateMessageText.setTextFill(Color.GREEN);
                    generateMessageText.setText(Constants.GENERATE_REPORT_SUCCESS);
                    messageDisappearanceTask(4000, generateMessageText);
                    System.out.println("Report generated successfully.");
                }
                else {
                    generateMessageText.setVisible(true);
                    generateMessageText.setTextFill(Color.RED);
                    generateMessageText.setText(Constants.GENERATE_REPORT_FAILED);
                    messageDisappearanceTask(4000, generateMessageText);
                    System.out.println("Generating report failed.");
                    return;
                }

            }
            else {
                generateMessageText.setVisible(true);
                generateMessageText.setTextFill(Color.RED);
                generateMessageText.setText(Constants.MISSING_REQUIREMENTS);
                messageDisappearanceTask(4000, generateMessageText);
                System.out.println("Insert required data.");
                return;
            }
            App.setCurrentReport1Displayed(report1);
        }
        viewReport(event);
    }

    @FXML
    void viewReport(ActionEvent event) {
        if (two_reports) {
            App.popUpLaunch(generateReportButton, "TwoReportsPopUp");
        }
        else {
            App.popUpLaunch(generateReportButton, "ReportPopUp");
        }
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
                    updated_complaint_text.setVisible(true);
                    updated_complaint_text.setText("Failed to close complaint");
                    updated_complaint_text.setTextFill(Color.RED);
                    messageDisappearanceTask(4000, updated_complaint_text);
                    return;
                }
                updated_complaint_text.setVisible(true);
                updated_complaint_text.setText("You have successfully closed the complaint");
                updated_complaint_text.setTextFill(Color.GREEN);
                messageDisappearanceTask(4000, updated_complaint_text);
            });

            updateComplaintTask.setOnFailed(e -> {
                // TODO: maybe properly log it somewhere
                updateComplaintTask.getException().printStackTrace();
                updated_complaint_text.setVisible(true);
                updated_complaint_text.setText("Failed to close complaint");
                updated_complaint_text.setTextFill(Color.RED);
                messageDisappearanceTask(4000, updated_complaint_text);
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

        String custUsername = customerComboBox.getValue();
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
        if(customerStatusComboBox.getValue()==null || selectedCustomer==null){
            System.out.println("Illegal selection, please select all necessary fields");
            editResultLabel.setText(Constants.ILLEGAL_SELECTION);
            editResultLabel.setTextFill(Color.RED);
            return;
        }
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
        //checks tthat all necessat fields are selected
        if(employeesTypeComboBox.getValue()==null || employeeStatusComboBox.getValue()==null
        || selectEmployeeComboBox.getValue()==null){
            System.out.println("Illegal selection, please select all necessary fields");
            editResultLabel.setText(Constants.ILLEGAL_SELECTION);
            editResultLabel.setTextFill(Color.RED);
            return;
        }
        if((employeeStatusComboBox.getValue().equals("Store Manager") ||
                employeeStatusComboBox.getValue().equals("Chain Employee")) &&
                selectStoreComboBox.getValue()==null){
            System.out.println("Illegal selection, please select all necessary fields");
            editResultLabel.setText(Constants.ILLEGAL_SELECTION);
            editResultLabel.setTextFill(Color.RED);
            return;
        }

        System.out.println("inserted into editEmployee");
        String selectedStatus = employeeStatusComboBox.getValue();

        String employeeName = selectEmployeeComboBox.getValue();
        Store selectedStore= null;
        Store oldStore = null;
        if(selectedStatus.equals("Chain Manager")) {
            for (Store store : storesList) {
                if ((store.getName().equals("Lilach Warehouse"))) {
                    selectedStore = store;
                    System.out.println("selected warehouse is: " + selectedStore.getName());
                    break;
                }
                //selectedStore = chainManager.getWarehouseManaged();
                //System.out.println("selected warehouse is: "+ selectedStore.getName());
            }
        }
        if(selectStoreComboBox.getValue()!=null){
            String storeName = selectStoreComboBox.getValue();
            if(storeName!=null) {
                for (Store store : storesList) {
                    if (storeName.equals(store.getName())) {
                        selectedStore = store;
                        System.out.println("selected store is: "+ selectedStore.getName());
                        break;
                    }
                }
            }
            selectStoreComboBox.getItems().clear();
        }
        if(employeesTypeComboBox.getValue().equals("Store Manager")){
            System.out.println("inserted into editEmployee store manager");
            StoreManager selectedManager = null;
            for(StoreManager manager: storeManagersList) {
                if (manager.getFullName().equals(employeeName)) {
                    selectedManager = manager;
                    oldStore = manager.getStoreManged();
                    System.out.println("old store in profiles is: "+ oldStore.getName());
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedManager, selectedStore, oldStore,selectedStatus, "Store Manager");
        }
        else if(employeesTypeComboBox.getValue().equals("Chain Employee")){
            System.out.println("inserted into editEmployee chain employee");
            ChainEmployee selectedEmployee = null;
            for (ChainEmployee employee: chainEmployeeList) {
                if (employee.getFullName().equals(employeeName)) {
                    selectedEmployee = employee;
                    oldStore = employee.getStore();
                    System.out.println("old store in profiles is: "+ oldStore.getName());
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedEmployee, selectedStore,  oldStore,selectedStatus, "Chain Employee");
        }
        else if (employeesTypeComboBox.getValue().equals("Customer Service")) {
            CustomerServiceEmployee selectedEmployee = null;
            for(CustomerServiceEmployee employee: customerServiceList) {
                if (employee.getFullName().equals(employeeName)) {
                    selectedEmployee = employee;
                    break;
                }
            }
            createTaskEmployeeUpdate(selectedEmployee, selectedStore, null,selectedStatus, "Customer Service");
        }

        else{//we're editing chain manager
            if (chainManager.getFullName().equals(employeeName)) {
                oldStore = chainManager.getWarehouseManaged();
                System.out.println("old store in profiles is: " + oldStore.getName());
            }
            createTaskEmployeeUpdate(chainManager, selectedStore, oldStore,selectedStatus, "Chain Manager");
        }
        employeesTypeComboBox.valueProperty().setValue(null); //edit to clear all
        selectStoreComboBox.valueProperty().setValue(null);
        employeeStatusComboBox.valueProperty().setValue(null);
        selectEmployeeComboBox.valueProperty().setValue(null);
        selectEmployeeComboBox.getItems().clear();
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
        reportsVBox.getChildren().remove(report2Vbox);
        if (App.getCurrentUser() != null) {
            welcomeText.setText("Welcome, " + App.getCurrentUser().getFullName());
        } else {
            welcomeText.setText("Welcome, unknown employee");
        }

        if (App.getCurrentUser().getClass() == CustomerServiceEmployee.class || App.getCurrentUser().getClass() == SystemAdmin.class) {
            catalogButton.setText("View Catalog");
        }

        addReportViewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reportsVBox.getChildren().add(1, report2Vbox);
                removeReportViewButton.setVisible(true);
                addReportViewButton.setVisible(false);
                two_reports = true;
            }
        });
        removeReportViewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reportsVBox.getChildren().remove(report2Vbox);
                removeReportViewButton.setVisible(false);
                addReportViewButton.setVisible(true);
                two_reports = false;
            }
        });

        //TODO: edit to show only to authorized users
        if (App.getCurrentUser() != null) {
            if(App.getCurrentUser().getClass()==ChainEmployee.class) {
                employeeControls.getPanes().removeAll(generateReportsPane, handleUsersPane, handleComplaintsPane);
            }
            if (App.getCurrentUser().getClass() == StoreManager.class) {
                chainReport.setVisible(false);
                storeComboBox.setDisable(true);
                StoreManager manager = (StoreManager)App.getCurrentUser();
                reportStore = manager.getStoreManged();
                report1Vbox.getChildren().remove(addReportViewButtonBox);
                reportsVBox.getChildren().remove(report2Vbox);
                employeeControls.getPanes().remove(handleUsersPane);
            }
            if (App.getCurrentUser().getClass() == CustomerServiceEmployee.class) {
                employeeControls.getPanes().removeAll(generateReportsPane, handleUsersPane);
            }
            if (App.getCurrentUser().getClass() == ChainManager.class) {
                employeeControls.getPanes().remove(handleUsersPane);
            }
            if (App.getCurrentUser().getClass() == SystemAdmin.class) {
                employeeControls.getPanes().removeAll(generateReportsPane, handleComplaintsPane);
            }
        }

        //calling db server tasks to get data
        initComplaints();
        initStores();
        initUsers();
        initEditEmployees();

    }
}
