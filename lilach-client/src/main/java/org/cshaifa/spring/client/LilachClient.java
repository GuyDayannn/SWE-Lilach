package org.cshaifa.spring.client;

import org.cshaifa.spring.client.ocsf.AbstractClient;

public class LilachClient extends AbstractClient {

    private Object msgFromServer = null;

    public LilachClient(String host, int port) {
        super(host, port);
    }

    /**
     * We assume that we send one msg per time
     */
    public Object getMsgFromServer() {
        while (msgFromServer != null) {}
        Object msg = msgFromServer;
        msgFromServer = null;
        return msg;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        msgFromServer = msg;
    }
}
