package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.CatalogItem;

/**
 * This request updates an item with all of the new details
 * provided to the server (for now)
 */
public class UpdateItemRequest extends Request {
    CatalogItem updatedItem;

    public UpdateItemRequest(CatalogItem updatedItem) {
        this.updatedItem = updatedItem;
    }

    public CatalogItem getUpdatedItem() {
        return updatedItem;
    }

    public void setUpdatedItem(CatalogItem updatedItem) {
        this.updatedItem = updatedItem;
    }
}
