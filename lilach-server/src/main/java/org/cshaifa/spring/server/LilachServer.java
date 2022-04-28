package org.cshaifa.spring.server;

import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.requests.GetCatalogRequest;
import org.cshaifa.spring.entities.requests.Request;
import org.cshaifa.spring.entities.requests.UpdateItemRequest;
import org.cshaifa.spring.entities.responses.GetCatalogResponse;
import org.cshaifa.spring.entities.responses.UpdateItemResponse;
import org.cshaifa.spring.server.database.DatabaseHandler;
import org.cshaifa.spring.server.ocsf.AbstractServer;
import org.cshaifa.spring.server.ocsf.ConnectionToClient;
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
            if (msg instanceof GetCatalogRequest) {
                try {
                    List<CatalogItem> catalogItems = DatabaseHandler.getCatalog();
                    sendToAllClients(new GetCatalogResponse(request.getRequestId(), catalogItems));
                } catch (HibernateException e) {
                    sendToAllClients(new GetCatalogResponse(request.getRequestId(), false));
                }
            } else if (msg instanceof UpdateItemRequest) {
                CatalogItem updatedItem = ((UpdateItemRequest)msg).getUpdatedItem();
                try {
                    DatabaseHandler.updateItem(updatedItem);
                    sendToAllClients(new UpdateItemResponse(request.getRequestId(), updatedItem));
                } catch (HibernateException e) {
                    sendToAllClients(new UpdateItemResponse(request.getRequestId(), false));
                }
            }
        } else {
            // TODO: Return an error message to the client
        }
    }

}
