package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chain_employee")
public class ChainEmployee extends Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public ChainEmployee(String fullName, String username, String email, String password) {
        super(fullName, username, email, password);
    }

    public ChainEmployee() {
        super();
    }

}
