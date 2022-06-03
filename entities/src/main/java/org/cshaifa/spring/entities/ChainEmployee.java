package org.cshaifa.spring.entities;

import javax.persistence.*;

@Entity
@Table(name = "chain_employees")
//@Inheritance(strategy = InheritanceType.JOINED)
public class ChainEmployee extends Employee {

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public ChainEmployee(String fullName, String username, String email, String password, String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public ChainEmployee() {
    }

    public Store getStore() {return store;}
    public void setStore(Store store) {this.store = store;}
}
