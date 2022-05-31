package org.cshaifa.spring.entities.responses;
import org.cshaifa.spring.entities.Order;

import java.util.List;

public class UpdateOrdersResponse extends Response{
    private Order order = null;

    public UpdateOrdersResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public UpdateOrdersResponse(int requestId, Order order) {
        super(requestId, true);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
