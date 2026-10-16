package com.example.app.turbojwt;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TurboJwt {

    private static final Gson gson = new Gson();

    // Équivalent de base64Encode pour générer du Base64 URL-safe sans padding (RawURLEncoding)[cite: 8]
    private static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    // Équivalent de generateHmac[cite: 8]
    private static String generateHmac(String secret, String unsignedToken) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
            return base64Encode(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC", e);
        }
    }

    // Équivalent de Encode[cite: 8]
    public static String encode(String secret, Map<String, Object> payload, double exp, double nbf) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        long currentTimeSec = System.currentTimeMillis() / 1000;
        long expiration = currentTimeSec + TimeUnit.HOURS.toSeconds((long) exp);
        long iat = currentTimeSec;

        payload.put("exp", (double) expiration);
        payload.put("iat", (double) iat);

        if (nbf > 0) {
            long notBefore = currentTimeSec + TimeUnit.MINUTES.toSeconds((long) nbf);
            payload.put("nbf", (double) notBefore);
        }

        String headerJson = gson.toJson(header);
        String encodedHeader = base64Encode(headerJson.getBytes(StandardCharsets.UTF_8));

        String payloadJson = gson.toJson(payload);
        String encodedPayload = base64Encode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = generateHmac(secret, unsignedToken);

        return unsignedToken + "." + signature;
    }

    // Équivalent de Verify[cite: 8]
    public static Map<String, Object> verify(String secret, String token, double leeway) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new Exception("Invalide Token");
        }

        String unsignedPart = parts[0] + "." + parts[1];
        String signature = generateHmac(secret, unsignedPart);

        // Comparaison sécurisée HMAC
        if (!MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new Exception("Invalide signature.");
        }

        byte[] payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        Map<String, Object> payload = gson.fromJson(new String(payloadBytes, StandardCharsets.UTF_8), new TypeToken<Map<String, Object>>(){}.getType());

        if (!payload.containsKey("exp")) {
            throw new Exception("Invalide token, missing exp time");
        }

        double exp = (double) payload.get("exp");
        long now = System.currentTimeMillis() / 1000;
        long leewaySec = (long) leeway;

        if (now > (long) exp) {
            throw new Exception("Expired Token");
        }

        if (now > ((long) exp + leewaySec)) {
            throw new Exception("Expired token");
        }

        if (payload.containsKey("nbf")) {
            double nbf = (double) payload.get("nbf");
            if (now < ((long) nbf - leewaySec)) {
                throw new Exception("Inactive token");
            }
        }

        return payload;
    }
}