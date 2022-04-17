package org.cshaifa.spring.client;

import org.cshaifa.spring.client.ocsf.AbstractClient;

public class LilachClient extends AbstractClient {

    public LilachClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void connectionEstablished() {
        // TODO Auto-generated method stub
        super.connectionEstablished();
        System.out.println("ESTABLISHED");
    }


    @Override
    protected void handleMessageFromServer(Object msg) {
        ClientHandler.msgFromServer = msg;
        System.out.println(ClientHandler.msgFromServer == null ? "MSG NULL" : "MSG NOT NULL");
    }
}
