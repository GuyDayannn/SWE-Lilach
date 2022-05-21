package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.Order;

public class CreateOrderResponse extends Response {

    private Order order;
    private String message;

    public CreateOrderResponse(int requestId, boolean success, Order order, String message) {
        super(requestId, success);
        this.order = order;
        this.message = message;
    }

    public Order getOrder() {
        return order;
    }

    public String getMessage() {
        return message;
    }

}
