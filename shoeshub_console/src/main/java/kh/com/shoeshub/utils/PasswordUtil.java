package kh.com.shoeshub.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hashes a plain text password with BCrypt (cost factor 12)
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Verifies a plain text password against the stored BCrypt hash
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
