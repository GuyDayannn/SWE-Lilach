package org.cshaifa.spring.client;

import org.cshaifa.spring.client.ocsf.AbstractClient;

public class LilachClient extends AbstractClient {

    public LilachClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        try {
          ClientHandler.msgQueue.put(msg);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }

    @Override
    protected void connectionClosed() {
        super.connectionClosed();
        ClientHandler.connectionClosed = true;
    }
}
