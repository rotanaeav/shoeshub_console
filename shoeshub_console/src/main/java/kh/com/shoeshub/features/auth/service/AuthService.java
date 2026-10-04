package kh.com.shoeshub.features.auth.service;

import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.auth.dto.LoginRequest;

public interface AuthService {

    AuthenticatedUser login(LoginRequest request);

}