package org.cshaifa.spring.entities;

public class Employee extends User {

    public Employee(String fullName, String username, String email, String password) {
        super(fullName, username, email, password);
    }

    public Employee() {
        super();
    }
}
