package kh.com.shoeshub.authorize;

import kh.com.shoeshub.features.auth.AuthenticatedUser;

public class Security {

    private AuthenticatedUser currentUser;

    public void authenticate(AuthenticatedUser user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Authenticated user cannot be null"
            );
        }

        this.currentUser = user;
    }

    public AuthenticatedUser getCurrentUser() {

        if (currentUser == null) {
            throw new SecurityException("User is not authenticated");
        }

        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
}