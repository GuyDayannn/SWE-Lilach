package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.Order;

import java.util.List;

public class GetOrdersResponse extends Response {
    List<Order> orderList = null;

    public GetOrdersResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public GetOrdersResponse(int requestId, List<Order> orderList) {
        super(requestId, true);
        this.orderList = orderList;
    }

    public List<Order> getOrdersList() {
        return orderList;
    }

}
