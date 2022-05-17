package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "chain_employees")
@Inheritance(strategy = InheritanceType.JOINED)
public class ChainEmployee extends Employee {

    public ChainEmployee(String fullName, String username, String email, String password, String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public ChainEmployee() {
    }

}
