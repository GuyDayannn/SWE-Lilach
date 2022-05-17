package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.User;

/**
 * This is the only request without a corresponding response
 */
public class LogoutRequest extends Request {

    private User user;

    public LogoutRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
