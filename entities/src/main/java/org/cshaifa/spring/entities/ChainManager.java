package org.cshaifa.spring.entities;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "chain_managers")
public class ChainManager extends StoreManager {
    @OneToOne
    private Store warehouseManaged;

    public ChainManager(String fullName, String username, String email, String password, String passwordSalt) {
        super(fullName, username, email, password, passwordSalt);
    }

    public ChainManager() {

    }

    public ChainManager(String fullName, String username, String email, String password, String passwordSalt, Store warehouse) {
        super(fullName, username, email, password, passwordSalt);
        this.setStoreManged(warehouse);
    }

    public Store getWarehouseManaged() {return warehouseManaged;}

    public void setWarehouseManaged(Store warehouse) {this.warehouseManaged = warehouse;}

    public void removeWarehouse(){this.warehouseManaged = null;}

}