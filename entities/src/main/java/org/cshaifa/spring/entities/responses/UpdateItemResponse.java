package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;

public class UpdateItemResponse extends Response {
    private CatalogItem updatedItem = null;

    public UpdateItemResponse(boolean success) {
        super(success);
    }

    public UpdateItemResponse(CatalogItem updatedItem) {
        super(true);
        this.updatedItem = updatedItem;
    }

    public CatalogItem getUpdatedItem() {
        return updatedItem;
    }
}
