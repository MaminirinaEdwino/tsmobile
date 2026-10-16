package com.example.app.activation;

import android.content.Context;
import android.util.Log;

import com.example.app.config.Config;
import com.example.app.turbojwt.TurboJwt;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activation {
    private static final String TAG = "Activation";

    // Équivalent de CheckActivationToken[cite: 9]
    public static Map<String, Object> checkActivationToken(Context context) {
        File turboStackDir = new File(Config.TURBO_STACK_DIR);
        File tokenFile = new File(turboStackDir, "token");

        Map<String, Object> resultMap = new HashMap<>();

        try {
            if (!tokenFile.exists()) {
                if (!turboStackDir.exists()) {
                    turboStackDir.mkdirs();
                }
                tokenFile.createNewFile();
                
                // Génération d'un token par défaut "free" expirant dans 10 heures[cite: 9]
                Map<String, Object> defaultPayload = new HashMap<>();
                defaultPayload.put("subscription", "free");
                String initialToken = TurboJwt.encode("secret", defaultPayload, 10, 0);
                
                Files.write(tokenFile.toPath(), initialToken.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }

            String tokenContent = new String(Files.readAllBytes(tokenFile.toPath()));
            Map<String, Object> res = null;
            boolean isExpired = false;

            try {
                res = TurboJwt.verify("secret", tokenContent, 0);
            } catch (Exception e) {
                // Si le token est expiré ou invalide, on essaie de le décoder quand même partiellement ou on gère l'erreur
                isExpired = true;
                // En cas d'expiration, verify lance une exception, on peut récupérer le contenu brut si besoin ou définir des valeurs par défaut
                res = new HashMap<>();
                res.put("subscription", "free");
                res.put("exp", (double) (System.currentTimeMillis() / 1000));
                res.put("iat", (double) (System.currentTimeMillis() / 1000));
            }

            double expVal = res.containsKey("exp") ? (double) res.get("exp") : (System.currentTimeMillis() / 1000);
            double iatVal = res.containsKey("iat") ? (double) res.get("iat") : (System.currentTimeMillis() / 1000);

            Date expTime = new Date((long) (expVal * 1000));
            Date iatTime = new Date((long) (iatVal * 1000));

            resultMap.put("subscription", res.get("subscription"));
            resultMap.put("exp", expTime.toString());
            resultMap.put("iat", iatTime.toString());

            if (isExpired) {
                resultMap.put("message", "old subscription has expired");
                resultMap.put("expired", "true");
            } else {
                resultMap.put("expired", "false");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erreur checkActivationToken", e);
            resultMap.put("error", e.getMessage());
        }

        return resultMap;
    }

    // Équivalent de SaveTokenToFile[cite: 9]
    public static void saveTokenToFile(String token) {
        try {
            File turboStackDir = new File(Config.TURBO_STACK_DIR);
            if (!turboStackDir.exists()) {
                turboStackDir.mkdirs();
            }
            File tokenFile = new File(turboStackDir, "token");
            Files.write(tokenFile.toPath(), token.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            Log.e(TAG, "Erreur saveTokenToFile", e);
        }
    }
}