package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;

public class NotifyUpdateResponse extends Response {

    private CatalogItem toUpdate;

    public NotifyUpdateResponse(CatalogItem toUpdate) {
        super(-1);
        this.toUpdate = toUpdate;
    }

}
