package com.example.app.utils;

public class Utils {

    // Équivalent de WriteErrorCheker[cite: 12]
    public static String writeErrorChecker(String message) {
        return String.format("if (err != null) {\n    System.out.println(\"%s\" + err);\n}", message);
    }

    // Équivalent de ErrorChecker[cite: 12]
    public static void errorChecker(Exception err) {
        if (err != null) {
            throw new RuntimeException(err);
        }
    }

    // Équivalent de ToUpperFirstLetter[cite: 12]
    public static String toUpperFirstLetter(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}