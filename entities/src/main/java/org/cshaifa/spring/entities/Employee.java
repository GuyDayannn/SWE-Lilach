package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee extends User {

    public Employee(String fullName, String username, String email, String password) {
        super(fullName, username, email, password);
    }

    public Employee() {
    }
}
