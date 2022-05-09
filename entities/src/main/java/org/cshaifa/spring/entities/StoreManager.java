package org.cshaifa.spring.entities;


import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "store_managers")
public class StoreManager extends ChainEmployee{

    @OneToOne
    private Store store;

    public StoreManager(String fullName, String username, String email, String password, Store store) {
        super(fullName, username, email, password);
        this.store = store;
    }

    public StoreManager() {
    }
}
