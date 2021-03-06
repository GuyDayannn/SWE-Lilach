package org.cshaifa.spring.server;

import java.io.IOException;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.ChainEmployee;
import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.Order;
import org.cshaifa.spring.entities.Store;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.AddComplaintRequest;
import org.cshaifa.spring.entities.requests.CreateItemRequest;
import org.cshaifa.spring.entities.requests.CreateOrderRequest;
import org.cshaifa.spring.entities.requests.DeleteItemRequest;
import org.cshaifa.spring.entities.requests.EditEmployeeRequest;
import org.cshaifa.spring.entities.requests.FreezeCustomerRequest;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.GetComplaintsRequest;
import org.cshaifa.spring.entities.requests.GetOrdersRequest;
import org.cshaifa.spring.entities.requests.GetStoresRequest;
import org.cshaifa.spring.entities.requests.GetUsersRequest;
import org.cshaifa.spring.entities.requests.IsAliveRequest;
import org.cshaifa.spring.entities.requests.LoginRequest;
import org.cshaifa.spring.entities.requests.LogoutRequest;
import org.cshaifa.spring.entities.requests.RegisterRequest;
import org.cshaifa.spring.entities.requests.Request;
import org.cshaifa.spring.entities.requests.UpdateComplaintRequest;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.requests.UpdateOrdersRequest;
import org.cshaifa.spring.entities.responses.AddComplaintResponse;
import org.cshaifa.spring.entities.responses.CreateItemResponse;
import org.cshaifa.spring.entities.responses.CreateOrderResponse;
import org.cshaifa.spring.entities.responses.DeleteItemResponse;
import org.cshaifa.spring.entities.responses.EditEmployeeResponse;
import org.cshaifa.spring.entities.responses.FreezeCustomerResponse;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.GetComplaintsResponse;
import org.cshaifa.spring.entities.responses.GetOrdersResponse;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.entities.responses.GetUsersResponse;
import org.cshaifa.spring.entities.responses.IsAliveResponse;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.LogoutResponse;
import org.cshaifa.spring.entities.responses.NotifyCreateResponse;
import org.cshaifa.spring.entities.responses.NotifyDeleteResponse;
import org.cshaifa.spring.entities.responses.NotifyUpdateResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.entities.responses.UpdateComplaintResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.entities.responses.UpdateOrdersResponse;
import org.cshaifa.spring.server.database.DatabaseHandler;
import org.cshaifa.spring.server.ocsf.AbstractServer;
import org.cshaifa.spring.server.ocsf.ConnectionToClient;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.EmailUtils;
import org.hibernate.HibernateException;

public class LilachServer extends AbstractServer {

    private static final String IMMEDIATE_ORDER_MAIL_SUBJECT = "Congratulation, Order has arrived!";
    private static final String PRODUCT_UPDATED_NOTIFICATION = "Product Updated";
    private static final String PRODUCT_DELETED_NOTIFICATION = "Product Deleted";
    private static final String PRODUCT_CREATED_NOTIFICATION = "Product Created";

    public LilachServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (msg instanceof Request) {
                Request request = (Request) msg;
                int requestId = request.getRequestId();
                if (request instanceof IsAliveRequest) {
                    client.sendToClient(new IsAliveResponse(requestId));
                } else if (request instanceof GetCatalogRequest) {
                    try {
                        List<CatalogItem> catalogItems = DatabaseHandler.getCatalog();
                        client.sendToClient(new GetCatalogResponse(requestId, catalogItems));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new GetCatalogResponse(requestId, false));
                    }
                } else if (request instanceof GetOrdersRequest) {
                    try {
                        List<Order> orderList = DatabaseHandler.getOrders();
                        client.sendToClient(new GetOrdersResponse(requestId, orderList));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new GetOrdersResponse(requestId, false));
                    }
                } else if (request instanceof GetUsersRequest) {
                    try {
                        List<User> userList = DatabaseHandler.getUsers();
                        client.sendToClient(new GetUsersResponse(requestId, userList));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new GetUsersResponse(requestId, false));
                    }
                } else if (request instanceof UpdateItemRequest updateItemRequest) {
                    CatalogItem updatedItem = updateItemRequest.getUpdatedItem();
                    try {
                        DatabaseHandler.updateItem(updatedItem);
                        client.sendToClient(new UpdateItemResponse(requestId, updatedItem));
                        sendToAllClients(new NotifyUpdateResponse(updateItemRequest.getEmployee(), updatedItem,
                                PRODUCT_UPDATED_NOTIFICATION));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new UpdateItemResponse(requestId, false));
                    }
                } else if (request instanceof EditEmployeeRequest editEmployeeRequest) {
                    ChainEmployee chainEmployee = editEmployeeRequest.getUpdatedChainEmployee();
                    Store store = editEmployeeRequest.getStore();
                    Store oldStore = editEmployeeRequest.getOldStore();
                    String strNewType = editEmployeeRequest.getNewType();
                    String strCurrType = editEmployeeRequest.getCurrType();
                    try {
                        DatabaseHandler.editEmployee(chainEmployee, store, oldStore, strNewType, strCurrType);
                        client.sendToClient(new EditEmployeeResponse(requestId, true, "edited"));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new EditEmployeeResponse(requestId, "false"));
                    }
                } else if (request instanceof DeleteItemRequest deleteItemRequest) {
                    CatalogItem updatedItem = deleteItemRequest.getItemToDelete();
                    try {
                        DatabaseHandler.deleteItem(updatedItem);
                        client.sendToClient(new DeleteItemResponse(requestId, true, "Deletion Completed"));
                        sendToAllClients(new NotifyDeleteResponse(deleteItemRequest.getEmployee(), updatedItem,
                                PRODUCT_DELETED_NOTIFICATION));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new DeleteItemResponse(requestId, false, "Deletion Failed"));
                    }

                } else if (request instanceof UpdateComplaintRequest updateComplaintRequest) {
                    Complaint updatedComplaint = updateComplaintRequest.getUpdatedComplaint();
                    try {
                        DatabaseHandler.updateComplaint(updatedComplaint);
                        client.sendToClient(new UpdateComplaintResponse(requestId, updatedComplaint));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new UpdateComplaintResponse(requestId, false));
                    }
                } else if (request instanceof UpdateOrdersRequest updateOrdersRequest) {
                    Order order = updateOrdersRequest.getUpdatedOrders();
                    try {
                        DatabaseHandler.updateOrders(order);
                        client.sendToClient(new UpdateOrdersResponse(requestId, order));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new UpdateOrdersResponse(requestId, false));
                    }
                } else if (request instanceof FreezeCustomerRequest freezeCustomerRequest) {
                    Customer customer = freezeCustomerRequest.getUpdatedCustomer();
                    boolean toFreeze = freezeCustomerRequest.getIfToFreeze();
                    try {
                        String msgFreeze = DatabaseHandler.freezeCustomer(customer, toFreeze);
                        client.sendToClient(new FreezeCustomerResponse(requestId, true, msgFreeze));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        String errorMsg = e.getMessage();
                        client.sendToClient(new FreezeCustomerResponse(requestId, errorMsg));
                    }

                } else if (request instanceof LoginRequest loginRequest) {
                    User user = DatabaseHandler.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
                    if (user != null && user.isLoggedIn()) {
                        client.sendToClient(new LoginResponse(requestId, false, Constants.ALREADY_LOGGED_IN));
                        return;
                    }
                    if (user instanceof Customer && ((Customer) user).isFrozen()) {
                        client.sendToClient(new LoginResponse(requestId, false, Constants.CUSTOMER_FROZEN_MSG));
                        return;
                    }
                    if (user != null) {
                        try {
                            DatabaseHandler.updateLoginStatus(user, true);
                        } catch (HibernateException e) {
                            e.printStackTrace();
                            client.sendToClient(new LoginResponse(requestId, false, Constants.FAIL_MSG));
                            return;
                        }
                    }
                    String message = user != null ? Constants.SUCCESS_MSG : Constants.WRONG_CREDENTIALS;
                    client.sendToClient(new LoginResponse(requestId, user != null, message, user));
                } else if (request instanceof RegisterRequest registerRequest) {
                    // We assume we login immediately after register
                    try {
                        String message = DatabaseHandler.registerCustomer(registerRequest.getFullName(),
                                registerRequest.getEmail(), registerRequest.getUsername(),
                                registerRequest.getPassword(), registerRequest.getStores(),
                                registerRequest.getSubscriptionType(), registerRequest.getCreditCard(),
                                registerRequest.getComplaintList());
                        if (message.equals(Constants.SUCCESS_MSG)) {
                            User user = DatabaseHandler.getUserByEmail(registerRequest.getEmail());
                            // TODO: maybe catch this separately
                            DatabaseHandler.updateLoginStatus(user, true);
                            client.sendToClient(new RegisterResponse(requestId, true, message, user));
                        } else {
                            client.sendToClient(new RegisterResponse(requestId, false, message));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        client.sendToClient(new RegisterResponse(requestId, false, Constants.FAIL_MSG));
                    }
                } else if (request instanceof LogoutRequest logoutRequest) {
                    try {
                        DatabaseHandler.updateLoginStatus(logoutRequest.getUser(), false);
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new LogoutResponse(requestId, false));
                        return;
                    }
                    client.sendToClient(new LogoutResponse(requestId, true));
                } else if (request instanceof CreateItemRequest createItemRequest) {
                    try {
                        CatalogItem item = DatabaseHandler.createItem(createItemRequest.getName(),
                                createItemRequest.getPrice(), createItemRequest.getQuantities(),
                                createItemRequest.isOnSale(), createItemRequest.getDiscountPercent(),
                                createItemRequest.getSize(), createItemRequest.getItemType(),
                                createItemRequest.getItemColor(), createItemRequest.isDefault(),
                                createItemRequest.getImage());
                        client.sendToClient(new CreateItemResponse(requestId, item != null, item));
                        sendToAllClients(new NotifyCreateResponse(item, createItemRequest.getEmployee(),
                                PRODUCT_CREATED_NOTIFICATION));
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new CreateItemResponse(requestId, false));
                    }
                } else if (request instanceof GetStoresRequest) {
                    client.sendToClient(new GetStoresResponse(requestId, DatabaseHandler.getStores()));
                } else if (request instanceof CreateOrderRequest createOrderRequest) {
                    try {
                        Store store = createOrderRequest.getStore();
                        if (store == null)
                            store = DatabaseHandler.getWarehouseStore();
                        Order order = DatabaseHandler.createOrder(store, createOrderRequest.getCustomer(), createOrderRequest.getCardNumber(),
                                createOrderRequest.getItems(), createOrderRequest.getGreeting(),
                                createOrderRequest.getOrderDate(), createOrderRequest.getSupplyDate(),
                                createOrderRequest.getDelivery(), createOrderRequest.getDeliveryDetails());
                        if (order != null) {
                            if (order.isDelivery()) {
                                String message = "<h2>Order #" + order.getId() + " has arrived!</h2>";
                                if (!order.getDeliveryDetails().getMessage().isBlank())
                                    message += "<br>" + order.getDeliveryDetails().getMessage();

                                if (order.getDeliveryDetails().isImmediate())
                                    EmailUtils.sendEmail(order.getCustomer().getEmail(),
                                            order.getCustomer().getFullName(), IMMEDIATE_ORDER_MAIL_SUBJECT,
                                            message);
                                else
                                    EmailUtils.sendEmailAt(order.getCustomer().getEmail(),
                                            order.getCustomer().getFullName(), IMMEDIATE_ORDER_MAIL_SUBJECT,
                                            message, order.getSupplyDate());
                            }
                            client.sendToClient(new CreateOrderResponse(requestId, true, order, Constants.SUCCESS_MSG));
                        } else {
                            client.sendToClient(new CreateOrderResponse(requestId, false, Constants.FAIL_MSG));
                        }
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new CreateOrderResponse(requestId, false, Constants.FAIL_MSG));
                    }
                } else if (request instanceof AddComplaintRequest addComplaintRequest) {
                    try {
                        Complaint complaint = DatabaseHandler.addComplaint(
                                addComplaintRequest.getComplaintDescription(), addComplaintRequest.getCustomer(),
                                addComplaintRequest.getStore());
                        if (complaint != null) {
                            client.sendToClient(
                                    new AddComplaintResponse(requestId, true, complaint, Constants.SUCCESS_MSG));
                        } else {
                            client.sendToClient(new AddComplaintResponse(requestId, false, Constants.FAIL_MSG));
                        }
                    } catch (HibernateException e) {
                        e.printStackTrace();
                        client.sendToClient(new AddComplaintResponse(requestId, false, Constants.FAIL_MSG));
                    }
                } else if (request instanceof GetComplaintsRequest) {
                    try {
                        List<Complaint> complaintsList = DatabaseHandler.getComplaints();
                        client.sendToClient(new GetComplaintsResponse(requestId, complaintsList));
                    } catch (HibernateException e) {
                        client.sendToClient(new GetComplaintsResponse(requestId, false));
                    }
                }
            } else {
                // TODO: Return a general error message to the client
            }
        } catch (IOException e) {
            // We couldn't send a msg to the client
            e.printStackTrace();
        }
    }

}
