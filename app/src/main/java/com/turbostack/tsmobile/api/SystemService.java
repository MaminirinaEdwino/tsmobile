package com.example.app.api;

import android.os.Build;
import com.example.app.entity.LibrairieList;

import java.util.HashMap;
import java.util.Map;

public class SystemService {

    public Map<String, String> getStats() {
        Map<String, String> stats = new HashMap<>();
        // Remplacement de runtime.GOOS / runtime.GOARCH par les informations de l'appareil Android
        stats.put("os", "android");
        stats.put("arch", Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : System.getProperty("os.arch"));
        return stats;
    }

    public LibrairieList loadLibrairie() {
        // Chargement de la librairie via l'entité Java équivalente
        return LibrairieList.loadLibrairie();
    }
}