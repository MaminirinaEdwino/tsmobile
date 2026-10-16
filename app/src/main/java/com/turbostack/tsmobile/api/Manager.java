package com.example.app.api;

import android.webkit.WebView;
import java.util.ArrayList;
import java.util.List;

public class Manager {

    // Interface équivalente à Binder en Go[cite: 6] pour exposer les objets au JavaScript sous Android
    public interface Binder {
        void bind(WebView webView);
    }

    private List<Binder> binders;

    public Manager() {
        this.binders = new ArrayList<>();
    }

    public void add(Binder b) {
        this.binders.add(b);
    }

    public void registerAll(WebView webView) {
        for (Binder b : binders) {
            b.bind(webView);
        }
    }
}