package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.User;

import java.util.List;

public class GetUsersResponse extends Response{
    List<User> users = null;
    public GetUsersResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public GetUsersResponse(int requestId, List<User> users) {
        super(requestId, true);
        this.users = users;
    }

    public List<User> getUsersList() {
        return users;
    }

}