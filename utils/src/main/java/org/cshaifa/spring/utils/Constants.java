package org.cshaifa.spring.utils;

public class Constants {
    // Messages
    public static final String DATABASE_ERROR = "database_error";
    public static final String SUCCESS_MSG = "Success";
    public static final String FAIL_MSG = "Fail";
    public static final String CUSTOMER_FROZEN_MSG = "Your account is frozen. Contact System Admin";
    public static final String WRONG_CREDENTIALS = "The username or password is incorrect";
    public static final String EMAIL_EXISTS = "A user with this email already exists";
    public static final String USERNAME_EXISTS = "A user with this username already exists";
    public static final String REGISTER_SUCCESS = "You have successfully registered for LiLach";
    public static final String LOGIN_SUCCESS = "You have successfully logged in to LiLach";
    public static final String ALREADY_LOGGED_IN = "You're already logged in on another device. Please log out, then try again";
    public static final String UPDATED_COMPLAINT = "You've successfully updated complaint";
    public static final String UPDATED_COMPLAINT_FAILED = "Couldn't update complaint";
    public static final String WAREHOUSE_NAME = "Lilach Warehouse";
    public static final String CANCEL_ORDER = "You've successfully deleted the order";
    public static final String CANCEL_ORDER_FAILED = "Couldn't delete the order";
    public static final String GENERATE_REPORT_SUCCESS = "Report generated successfully";
    public static final String GENERATE_REPORT_FAILED = "Generating report failed";
    public static final String MISSING_REQUIREMENTS = "Insert required data";
    public static final String EDIT_EMPLOYEE_SUCCESS = "Successfully edited employee status";
    public static final String EDIT_EMPLOYEE_FAILED = "Failed to edit employee status";
    public static final String EDIT_CUSTOMER_SUCCESS = "Successfully edited customer status";
    public static final String EDIT_CUSTOMER_FAILED = "Failed to edit customer status";
    public static final String ILLEGAL_SELECTION = "Illegal selection, not all fields are set. Please try again.";


    public static final int SERVER_PORT = 8167;
    public static final long REQUEST_TIMEOUT = 15;
    public static final long LOADING_TIMEOUT = REQUEST_TIMEOUT;
    public static final long UPDATE_INTERVAL = 5;
}
