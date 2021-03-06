package org.cshaifa.spring.client;

import org.cshaifa.spring.client.ocsf.AbstractClient;
import org.cshaifa.spring.entities.responses.NotifyResponse;

public class LilachClient extends AbstractClient {

    public LilachClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof NotifyResponse notifyResponse) {
            try {
                ClientHandler.updateQueue.put(notifyResponse);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return;
        }

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
