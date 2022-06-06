package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.Employee;

public abstract class NotifyResponse extends Response {

    private String message;
    private Employee sendingEmployee;

    public NotifyResponse(Employee sendingEmployee, String message) {
        super(-1);
        this.sendingEmployee = sendingEmployee;
        this.message = message;
    }

    public Employee getSendingEmployee() {
        return sendingEmployee;
    }

    public String getMessage() {
        return message;
    }

}
