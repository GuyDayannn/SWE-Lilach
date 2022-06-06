package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chain_managers")
public class ChainManager extends ChainEmployee {
    public ChainManager(String fullName, String username, String email, String password, String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public ChainManager() {
    }
}