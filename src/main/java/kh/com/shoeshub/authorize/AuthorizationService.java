package kh.com.shoeshub.authorize;

import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.user.UserRole;

public class AuthorizationService {

    private final Security security;

    public AuthorizationService(Security security) {
        this.security = security;
    }


    public AuthenticatedUser requireAuthenticated() {
        if (!security.isAuthenticated()) {
            throw new SecurityException("Please login first.");
        }

        return security.getCurrentUser();
    }

    public AuthenticatedUser requireAnyRole(UserRole... roles) {
        AuthenticatedUser user = requireAuthenticated();

        for (UserRole role : roles) {
            if (user.role() == role) {
                return user;
            }
        }

        throw new SecurityException("You don't have permission to perform this action.");
    }

    public boolean hasRole(UserRole role) {
        return security.isAuthenticated() && security.getCurrentUser() != null && security.getCurrentUser().role() == role;
    }
}