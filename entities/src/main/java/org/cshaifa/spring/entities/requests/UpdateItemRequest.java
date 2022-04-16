package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.CatalogItem;

/**
 * This request updates an item with all of the new details
 * provided to the server (for now)
 */
public class UpdateItemRequest extends Request {
    long itemID;
    CatalogItem updatedItem;

    public UpdateItemRequest(long itemID, CatalogItem updatedItem) {
        this.itemID = itemID;
        this.updatedItem = updatedItem;
    }

}
