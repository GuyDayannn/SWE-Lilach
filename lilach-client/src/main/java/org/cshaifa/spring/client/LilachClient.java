package org.cshaifa.spring.client;

import org.cshaifa.spring.client.ocsf.AbstractClient;

public class LilachClient extends AbstractClient {

    public LilachClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        ClientHandler.msgFromServer = msg;
    }

    @Override
    protected void connectionClosed() {
        super.connectionClosed();
        ClientHandler.connectionClosed = true;
    }
}
