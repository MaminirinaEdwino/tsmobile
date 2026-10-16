package com.example.app.service;

import java.io.File;

public class FileChecker {

    // Équivalent de FileExists[cite: 11]
    public static boolean fileExists(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile();
    }
}