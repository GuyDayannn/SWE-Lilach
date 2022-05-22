package org.cshaifa.spring.server;

import java.util.ArrayList;
import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Complaint;
import org.cshaifa.spring.entities.Customer;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.*;
import org.cshaifa.spring.entities.responses.*;
import org.cshaifa.spring.server.database.DatabaseHandler;
import org.cshaifa.spring.server.ocsf.AbstractServer;
import org.cshaifa.spring.server.ocsf.ConnectionToClient;
import org.cshaifa.spring.utils.Constants;
import org.hibernate.HibernateException;

public class LilachServer extends AbstractServer {

    public LilachServer(int port) {
        super(port);
    }

    @Override
    protected void serverClosed() {
        DatabaseHandler.closeSession();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof Request) {
            Request request = (Request) msg;
            int requestId = request.getRequestId();
            if (request instanceof GetCatalogRequest) {
                try {
                    List<CatalogItem> catalogItems = DatabaseHandler.getCatalog();
                    sendToAllClients(new GetCatalogResponse(requestId, catalogItems));
                } catch (HibernateException e) {
                    sendToAllClients(new GetCatalogResponse(requestId, false));
                }
            } else if (request instanceof UpdateItemRequest updateItemRequest) {
                CatalogItem updatedItem = updateItemRequest.getUpdatedItem();
                try {
                    DatabaseHandler.updateItem(updatedItem);
                    sendToAllClients(new UpdateItemResponse(requestId, updatedItem));
                } catch (HibernateException e) {
                    sendToAllClients(new UpdateItemResponse(requestId, false));
                }
            } else if (request instanceof LoginRequest loginRequest) {
                User user = DatabaseHandler.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
                if (user != null && user.isLoggedIn()) {
                    sendToAllClients(new LoginResponse(requestId, false, Constants.ALREADY_LOGGED_IN));
                    return;
                }
                if (user != null) {
                    try {
                        DatabaseHandler.updateLoginStatus(user, true);
                    } catch (HibernateException e) {
                        sendToAllClients(new LoginResponse(requestId, false, Constants.FAIL_MSG));
                        return;
                    }
                }
                String message = user != null ? Constants.SUCCESS_MSG : Constants.WRONG_CREDENTIALS;
                sendToAllClients(new LoginResponse(requestId, user != null, message, user));
            } else if (request instanceof RegisterRequest registerRequest) {
                // We assume we login immediately after register
                try {
                    List<Complaint> complaintList= new ArrayList<>(); //always empty on initialization
                    String message = DatabaseHandler.registerCustomer(registerRequest.getFullName(),
                            registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword(),
                            registerRequest.getStores(), registerRequest.getSubscriptionType() ,complaintList);
                    if (message.equals(Constants.SUCCESS_MSG)) {
                        User user = DatabaseHandler.getUserByEmail(registerRequest.getEmail());
                        // TODO: maybe catch this separately
                        DatabaseHandler.updateLoginStatus(user, true);
                        sendToAllClients(new RegisterResponse(requestId, true, message, user));
                    } else {
                        sendToAllClients(new RegisterResponse(requestId, false, message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToAllClients(new RegisterResponse(requestId, false, Constants.FAIL_MSG));
                }
            } else if (request instanceof LogoutRequest logoutRequest) {
                try {
                    DatabaseHandler.updateLoginStatus(logoutRequest.getUser(), false);
                } catch (HibernateException e) {
                    sendToAllClients(new LogoutResponse(requestId, false));
                    return;
                }

                sendToAllClients(new LogoutResponse(requestId, true));
            } else if (request instanceof GetStoresRequest) {
                sendToAllClients(new GetStoresResponse(requestId, DatabaseHandler.getStores()));
            }else if (request instanceof GetItemRequest getItemRequest){
                long itemID = getItemRequest.getItemID();
                try {
                    CatalogItem catalogItem = DatabaseHandler.getItem(itemID);
                    sendToAllClients(new GetItemResponse(requestId, catalogItem!=null, catalogItem ));
                } catch (HibernateException e) {
                    sendToAllClients(new GetItemResponse(requestId, false));
                }
            }else if (request instanceof GetComplaintsRequest getComplaintRequest){
                try {
                    List<Complaint> complaintsList = DatabaseHandler.getComplaints();
                    sendToAllClients(new GetComplaintsResponse(requestId, complaintsList ));
                } catch (HibernateException e) {
                    sendToAllClients(new GetComplaintsResponse(requestId, false));
                }
            }
            else if (request instanceof GetCustomerRequest getCustomerRequest){
                try {
                    long customerID = getCustomerRequest.getCustomerID();
                    Customer customer = DatabaseHandler.getCustomer(customerID);
                    sendToAllClients(new GetCustomerResponse(requestId, customer != null, customer ));
                } catch (HibernateException e) {
                    sendToAllClients(new GetCustomerResponse(requestId, false));
                }
            }


        } else {
            // TODO: Return a general error message to the client
        }
    }


}
