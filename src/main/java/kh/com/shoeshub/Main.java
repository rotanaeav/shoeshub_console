package kh.com.shoeshub;

import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.ui.Menu;

public class Main {

     static void main() {
         Menu.loginForTest(UserRole.SELLER);
         Menu.displaySellerMenu();

     }
}
