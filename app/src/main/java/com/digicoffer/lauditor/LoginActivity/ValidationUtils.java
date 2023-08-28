package com.digicoffer.lauditor.LoginActivity;

import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // At least 8 characters, containing at least one letter and one digit
        return password != null && Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{8,}$").matcher(password).matches();


    }

}
