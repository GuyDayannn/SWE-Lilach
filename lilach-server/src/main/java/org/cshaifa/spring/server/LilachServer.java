package org.cshaifa.spring.server;

import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.CreateItemRequest;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.GetStoresRequest;
import org.cshaifa.spring.entities.requests.LoginRequest;
import org.cshaifa.spring.entities.requests.LogoutRequest;
import org.cshaifa.spring.entities.requests.RegisterRequest;
import org.cshaifa.spring.entities.requests.Request;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.CreateItemResponse;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.GetStoresResponse;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.LogoutResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
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
                    String message = DatabaseHandler.registerCustomer(registerRequest.getFullName(),
                            registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword(),
                            registerRequest.getStores(), registerRequest.getSubscriptionType());
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
            } else if (request instanceof CreateItemRequest createItemRequest) {
                try {
                    CatalogItem item = DatabaseHandler.createItem(createItemRequest.getName(), createItemRequest.getPrice(), createItemRequest.isOnSale(), createItemRequest.getDiscountPercent(), createItemRequest.getSize(), createItemRequest.getItemType(), createItemRequest.getItemColor(), createItemRequest.getImage());
                    sendToAllClients(new CreateItemResponse(requestId, item != null, item));
                } catch (HibernateException e) {
                    e.printStackTrace();
                    sendToAllClients(new CreateItemResponse(requestId, false));
                }
            }
        } else {
            // TODO: Return a general error message to the client
        }
    }

}
