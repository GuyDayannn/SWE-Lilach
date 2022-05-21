package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;

import java.util.List;

public class GetItemResponse extends Response{
    CatalogItem item = null;
    List<CatalogItem> catalogItemList = null;

    public GetItemResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public GetItemResponse(int requestId, long itemID) {
        super(requestId, true);
        this.item = catalogItemList.get((int) itemID);
    }

    public CatalogItem getItem() {
        return item;
    }
}

