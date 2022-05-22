package org.cshaifa.spring.entities.requests;

/**
 * This request gets an entire item's details
 */
public class GetItemRequest extends Request {
    long itemID;

    public GetItemRequest(long itemID) {
        this.itemID = itemID;
    }

    public long getItemID() {
        return itemID;
    }
}
