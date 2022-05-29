package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.Order;

import java.util.List;

public class UpdateOrdersRequest extends Request {
    Order order;

    public UpdateOrdersRequest(Order order) {
        this.order = order;
    }

    public Order getUpdatedOrders() {
        return order;
    }

    public void setOrderList(Order order) {
        this.order = order;
    }
}
