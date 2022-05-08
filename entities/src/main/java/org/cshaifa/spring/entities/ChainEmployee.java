package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chain_employees")
public class ChainEmployee extends Employee {

    public ChainEmployee(String fullName, String username, String email, String password) {
        super(fullName, username, email, password);
    }

    public ChainEmployee() {
    }

}
