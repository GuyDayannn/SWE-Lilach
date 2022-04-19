package org.cshaifa.spring.entities.responses;

import java.util.List;

import org.cshaifa.spring.entities.CatalogItem;

public class GetCatalogResponse extends Response {
    List<CatalogItem> catalogItems = null;

    public GetCatalogResponse(boolean success) {
        super(success);
    }

    public GetCatalogResponse(List<CatalogItem> catalogItems) {
        super(true);
        this.catalogItems = catalogItems;
    }

    public List<CatalogItem> getCatalogItems() {
        return catalogItems;
    }
}
