package org.cshaifa.spring.entities.requests;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is a base class which all requests subclasses derive from
 */
public abstract class Request implements Serializable {

    private static AtomicInteger nextRequestId = new AtomicInteger(1);

    private int requestId;

    public Request() {
        this.requestId = nextRequestId.getAndIncrement();
    }

    public int getRequestId() {
        return requestId;
    }

}
