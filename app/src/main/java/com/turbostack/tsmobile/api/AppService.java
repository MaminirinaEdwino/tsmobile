package com.example.app.api;

import android.webkit.WebView;

public class AppService {

    /**
     * Équivalent de la fonction Dispatch en Go pour une WebView Android.
     * Permet d'injecter et d'exécuter un événement personnalisé JavaScript (CustomEvent) dans la WebView.
     *
     * @instance L'instance de la WebView Android
     * @param eventName Le nom de l'événement (ex: "get-status-event")
     * @param data Les données au format JSON sous forme de chaîne de caractères
     */
    public static void dispatch(WebView webView, String eventName, String data) {
        if (webView != null) {
            String script = "window.dispatchEvent(new CustomEvent(\"" + eventName + "\", {detail: " + data + "}));";
            
            // Exécution sur le thread principal de l'interface utilisateur requis par Android WebView
            webView.post(() -> webView.evaluateJavascript(script, null));
        }
    }
}