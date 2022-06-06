package org.cshaifa.spring.entities;


import javax.persistence.*;

@Entity
@Table(name = "store_managers")
public class StoreManager extends ChainEmployee{

    @OneToOne
    private Store storeManaged;

    public StoreManager(String fullName, String username, String email, String password, String passwordSalt,
            Store store) {
        super(fullName, username, email, password, passwordSalt);
        this.storeManaged = store;
    }

    public StoreManager(String fullName, String username, String email, String password, String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public StoreManager() {
    }

    public Store getStoreManged() {return storeManaged;}
    public void setStoreManged(Store store) {this.storeManaged = store;}

    public void removeStoreManaged(){this.storeManaged = null;}
}
