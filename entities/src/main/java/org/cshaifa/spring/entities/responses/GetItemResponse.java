package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;

import java.util.List;

public class GetItemResponse extends Response{
    private CatalogItem item;
    List<CatalogItem> catalogItemList = null;

    public GetItemResponse(int requestId, boolean success) {
        super(requestId, success);
        this.item = null;
    }

    public GetItemResponse(int requestId, boolean success, CatalogItem catalogItem) {
        super(requestId, success);
        this.item = catalogItem;
        //this.item = catalogItemList.get((int) itemID);
    }

    public CatalogItem getItem() {
        return item;
    }
}

