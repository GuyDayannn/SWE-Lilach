package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Employee;

public class DeleteItemRequest extends Request{
    private CatalogItem itemToDelete;
    private Employee employee;

    public DeleteItemRequest(Employee employee, CatalogItem itemToDelete) {
        this.itemToDelete = itemToDelete;
        this.employee = employee;
    }

    public CatalogItem getItemToDelete() { return itemToDelete; }

    public Employee getEmployee() {
        return employee;
    }
}
