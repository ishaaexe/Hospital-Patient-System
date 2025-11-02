package com.hospital.controller;

/**
 * LoginController.java
 * This class handles the logic for validating a user's login credentials.
 * It's a new file for the login system.
 */
public class LoginController {

    private final String REQUIRED_DOMAIN = "@newlife.care";
    private final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Validates the user's login attempt.
     *
     * @param username The email address entered by the user.
     * @param password The password entered by the user.
     * @return A validation message (either "Success" or an error message).
     */
    public String validateLogin(String username, char[] password) {
        
        // 1. Check if username is empty
        if (username == null || username.trim().isEmpty()) {
            return "Error: Email address cannot be empty.";
        }

        // 2. Check for the required email domain
        if (!username.trim().toLowerCase().endsWith(REQUIRED_DOMAIN)) {
            return "Error: Access denied!";
        }

        // 3. Check for password
        if (password == null || password.length < MIN_PASSWORD_LENGTH) {
            return "Error: Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.";
        }

        // 4. If all checks pass
        return "Success";
    }
}
