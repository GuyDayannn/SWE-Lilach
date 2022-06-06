package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Employee;

/**
 * This request updates an item with all of the new details
 * provided to the server (for now)
 */
public class UpdateItemRequest extends Request {
    CatalogItem updatedItem;
    Employee employee;

    public UpdateItemRequest(Employee employee, CatalogItem updatedItem) {
        this.employee = employee;
        this.updatedItem = updatedItem;
    }

    public CatalogItem getUpdatedItem() {
        return updatedItem;
    }

    public Employee getEmployee() {
        return employee;
    }
}
