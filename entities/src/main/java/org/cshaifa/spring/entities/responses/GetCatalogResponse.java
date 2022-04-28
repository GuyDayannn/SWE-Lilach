package org.cshaifa.spring.entities.responses;

import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;

public class GetCatalogResponse extends Response {
    List<CatalogItem> catalogItems = null;

    public GetCatalogResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public GetCatalogResponse(int requestId, List<CatalogItem> catalogItems) {
        super(requestId, true);
        this.catalogItems = catalogItems;
    }

    public List<CatalogItem> getCatalogItems() {
        return catalogItems;
    }
}
