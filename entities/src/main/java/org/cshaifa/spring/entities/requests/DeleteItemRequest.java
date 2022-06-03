package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.CatalogItem;

public class DeleteItemRequest extends Request{
    private CatalogItem itemToDelete;

    public DeleteItemRequest(CatalogItem itemToDelete) {
        this.itemToDelete = itemToDelete;
    }

    public CatalogItem getItemToDelete() { return itemToDelete; }
}
