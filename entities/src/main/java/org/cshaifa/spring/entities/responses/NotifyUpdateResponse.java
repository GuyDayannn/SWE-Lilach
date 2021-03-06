package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Employee;

public class NotifyUpdateResponse extends NotifyResponse {

    private CatalogItem toUpdate;

    public NotifyUpdateResponse(Employee sendingEmployee, CatalogItem toUpdate, String message) {
        super(sendingEmployee, message);
        this.toUpdate = toUpdate;
    }

    public CatalogItem getToUpdate() {
        return toUpdate;
    }
}
