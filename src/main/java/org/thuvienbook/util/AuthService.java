package org.thuvienbook.util;

import java.util.List;

import org.thuvienbook.model.User;

public class AuthService {
    private final List<User> users;

    public AuthService(List<User> users) {
        this.users = users;
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.checkCredentials(username, password)) {
                return u;
            }
        }
        return null;
    }
}
