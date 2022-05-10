package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "customer_service_employees")
public class CustomerServiceEmployee extends Employee{

    public CustomerServiceEmployee(String fullName, String username, String email, String password,
            String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public CustomerServiceEmployee() {
    }
}
