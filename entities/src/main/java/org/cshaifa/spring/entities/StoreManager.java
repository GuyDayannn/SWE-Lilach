package org.cshaifa.spring.entities;


import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "store_managers")
public class StoreManager extends ChainEmployee{

    @OneToOne
    private Store store;

    public StoreManager(String fullName, String username, String email, String password, String passwordSalt,
            Store store) {
        super(fullName, username, email, password, passwordSalt);
        this.store = store;
    }

    public StoreManager() {
    }
}
