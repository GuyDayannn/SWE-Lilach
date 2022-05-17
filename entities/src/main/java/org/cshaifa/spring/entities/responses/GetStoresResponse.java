package org.cshaifa.spring.entities.responses;

import java.util.List;

import org.cshaifa.spring.entities.Store;

public class GetStoresResponse extends Response {

    List<Store> stores = null;

    public GetStoresResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public GetStoresResponse(int requestId, List<Store> stores) {
        super(requestId, true);
        this.stores = stores;
    }

    public List<Store> getStores() {
        return stores;
    }
}
