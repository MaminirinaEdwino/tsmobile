package com.example.app.api;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.example.app.activation.Activation; // Remplacez par le package réel de votre classe d'activation

import java.util.HashMap;
import java.util.Map;

public class UserService implements Manager.Binder {

    @JavascriptInterface
    public String sayHello(String name) {
        return "Bonjour " + name;
    }

    @JavascriptInterface
    public String saveToken(String token) {
        Activation.saveTokenToFile(token); // Équivalent de l'appel d'activation en Go[cite: 7]
        return "Token Saved";
    }

    @JavascriptInterface
    public Map<String, Object> checkToken() {
        return Activation.checkActivationToken(); // Équivalent de l'appel d'activation en Go[cite: 7]
    }

    @Override
    public void bind(WebView webView) {
        // Sous Android, l'exposition des méthodes au JavaScript se fait via addJavascriptInterface
        // et l'annotation @JavascriptInterface sur les méthodes ci-dessus.
        // L'objet sera accessible en JS via le nom "Android" (par exemple: Android.sayHello("MonNom"))
        webView.addJavascriptInterface(this, "Android");
    }
}