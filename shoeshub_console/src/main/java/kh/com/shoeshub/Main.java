package kh.com.shoeshub;


import kh.com.shoeshub.features.auth.AuthUI;
import kh.com.shoeshub.features.user.UserUI;

public class Main {

    static void main() {
        new AuthUI();
        new UserUI();
    }
}