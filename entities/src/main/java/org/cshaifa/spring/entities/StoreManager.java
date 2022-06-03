package org.cshaifa.spring.entities;


import javax.persistence.*;

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

    public StoreManager(String fullName, String username, String email, String password, String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public StoreManager() {
    }

    public Store getStore() {return store;}
    public void setStore(Store store) {this.store = store;}
}
