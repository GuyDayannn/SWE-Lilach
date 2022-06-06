package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Employee;

public class NotifyDeleteResponse extends NotifyResponse {

    private CatalogItem toDelete;

    public NotifyDeleteResponse(Employee sendingEmployee, CatalogItem toDelete) {
        super(sendingEmployee);
        this.toDelete = toDelete;
    }

    public CatalogItem getToDelete() {
        return toDelete;
    }
}
