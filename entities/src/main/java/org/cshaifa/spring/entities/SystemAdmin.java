package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "system_admins")
public class SystemAdmin extends Employee{
    public SystemAdmin(String fullName, String username, String email, String password) {
        super(fullName, username, email, password);
    }

    public SystemAdmin() {
    }
}
