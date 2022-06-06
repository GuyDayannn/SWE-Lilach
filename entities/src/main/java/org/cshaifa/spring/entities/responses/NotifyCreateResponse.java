package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Employee;

public class NotifyCreateResponse extends NotifyResponse {

    private CatalogItem toCreate;

    public NotifyCreateResponse(CatalogItem toCreate, Employee sendingEmployee, String message) {
        super(sendingEmployee, message);
        this.toCreate = toCreate;
    }

    public CatalogItem getToCreate() {
        return toCreate;
    }
}
