package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.Employee;

public abstract class NotifyResponse extends Response {

    private Employee sendingEmployee;

    public NotifyResponse(Employee sendingEmployee) {
        super(-1);
        this.sendingEmployee = sendingEmployee;
    }

    public Employee getSendingEmployee() {
        return sendingEmployee;
    }

}
