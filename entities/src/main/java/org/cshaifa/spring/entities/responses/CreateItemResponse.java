package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;

public class CreateItemResponse extends Response {

    private CatalogItem item;

    public CreateItemResponse(int requestId, boolean success) {
        super(requestId, success);
        this.item = null;
    }

    public CreateItemResponse(int requestId, boolean success, CatalogItem item) {
        super(requestId, success);
        this.item = item;
    }

    public CatalogItem getItem() {
        return item;
    }

}
