package org.cshaifa.spring.server;

import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.User;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.LoginRequest;
import org.cshaifa.spring.entities.requests.RegisterRequest;
import org.cshaifa.spring.entities.requests.Request;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.LoginResponse;
import org.cshaifa.spring.entities.responses.RegisterResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.server.database.DatabaseHandler;
import org.cshaifa.spring.server.ocsf.AbstractServer;
import org.cshaifa.spring.server.ocsf.ConnectionToClient;
import org.cshaifa.spring.utils.Constants;
import org.hibernate.HibernateException;

import jdk.vm.ci.meta.Constant;

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
            } else if (request instanceof UpdateItemRequest) {
                CatalogItem updatedItem = ((UpdateItemRequest)request).getUpdatedItem();
                try {
                    DatabaseHandler.updateItem(updatedItem);
                    sendToAllClients(new UpdateItemResponse(requestId, updatedItem));
                } catch (HibernateException e) {
                    sendToAllClients(new UpdateItemResponse(requestId, false));
                }
            } else if (request instanceof LoginRequest) {
                LoginRequest loginRequest = (LoginRequest) request;
                User user = DatabaseHandler.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
                String message = user != null ? Constants.SUCCESS_MSG : Constants.WRONG_CREDENTIALS;
                sendToAllClients(new LoginResponse(requestId, user != null, message, user));
            } else if (request instanceof RegisterRequest) {
                RegisterRequest registerRequest = (RegisterRequest) request;
                try {
                    String message = DatabaseHandler.registerCustomer(registerRequest.getFullName(), registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword());
                    if (message.equals(Constants.SUCCESS_MSG)) {
                        User user = DatabaseHandler.getUserByEmail(registerRequest.getEmail());
                        sendToAllClients(new RegisterResponse(requestId, true, message, user));
                    } else {
                        sendToAllClients(new RegisterResponse(requestId, false, message));
                    }
                } catch (HibernateException e) {
                    sendToAllClients(new RegisterResponse(requestId, false, Constants.FAIL_MSG));
                }
            }
        } else {
            // TODO: Return an error message to the client
        }
    }

}
