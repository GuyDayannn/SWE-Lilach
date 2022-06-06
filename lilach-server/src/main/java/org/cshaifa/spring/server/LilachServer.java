package org.cshaifa.spring.server;

import org.cshaifa.spring.entities.*;
import org.cshaifa.spring.entities.requests.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.server.database.DatabaseHandler;
import org.cshaifa.spring.server.ocsf.AbstractServer;
import org.cshaifa.spring.server.ocsf.ConnectionToClient;
import org.cshaifa.spring.utils.Constants;
import org.cshaifa.spring.utils.EmailUtils;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.util.List;

public class LilachServer extends AbstractServer {

    private static final String IMMEDIATE_ORDER_MAIL_SUBJECT = "Your order has arrived!";
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
                        Order order = DatabaseHandler.createOrder(store, createOrderRequest.getCustomer(),
                                createOrderRequest.getItems(), createOrderRequest.getGreeting(),
                                createOrderRequest.getOrderDate(), createOrderRequest.getSupplyDate(),
                                createOrderRequest.getDelivery(), createOrderRequest.getDeliveryDetails());
                        if (order != null) {
                            if (order.isDelivery()) {
                                String message = "<h2>Your order #" + order.getId() + " has arrived!</h2>";
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
