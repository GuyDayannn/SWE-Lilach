package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.*;

public class EditEmployeeRequest extends Request{
    ChainEmployee chainEmployee;
    Store store;
    String newType;
    String currType;

    public EditEmployeeRequest(ChainEmployee chainEmployee, Store store, String newType, String currType){
        this.chainEmployee = chainEmployee;
        this.store = store;
        this.newType = newType;
        this.currType = currType;
    }

    public ChainEmployee getUpdatedChainEmployee() {return chainEmployee;}
    public Store getStore() {return store;}
    public String getNewType() {return newType;}
    public String getCurrType() { return  currType;}

    public CustomerServiceEmployee getUpdatedServiceEmployee() {return (CustomerServiceEmployee) chainEmployee;}
    public StoreManager getUpdatedStoreManager() {return (StoreManager) chainEmployee;}
    public ChainManager getUpdatedChainManager() {return (ChainManager) chainEmployee;}


}
