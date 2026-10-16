package com.turbostack.tsmobile;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.util.Log;

import com.turbostack.tsmobile.api.Manager;
import com.turbostack.tsmobile.api.SystemService;
import com.turbostack.tsmobile.api.UserService;
import com.turbostack.tsmobile.api.ProjectService;
import com.turbostack.tsmobile.activation.Activation;
import com.turbostack.tsmobile.config.Config;
import com.turbostack.tsmobile.entity.ProjectManager;
import com.turbostack.tsmobile.entity.LibrairieList;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        getWindow().setStatusBarColor(android.graphics.Color.parseColor("#6200EE"));

        // 1. Initialisation de la configuration des chemins pour Android (équivalent de config)[cite: 13]
        Config.init(this);

        // 2. Gestion des permissions de stockage externe
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) 
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        WebView webView = (WebView) findViewById(R.id.my_webview);
        WebSettings settings = webView.getSettings();
        
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // 3. Initialisation du ProjectManager et chargement des projets existants[cite: 13]
        ProjectManager projectMgr = new ProjectManager();
        try {
            projectMgr.loadProjects();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des projets : " + e.getMessage());
        }

        // 4. Vérification du token d'activation de l'application[cite: 13]
        Activation.checkActivationToken(this);

        // 5. Initialisation du Manager et enregistrement des services (UserService, SystemService, ProjectService)[cite: 13]
        Manager mgr = new Manager();
        mgr.add(new UserService());
        mgr.add(new SystemService());
        mgr.add(new ProjectService(this, projectMgr));

        // 6. Vérification et création des dossiers et fichiers de configuration si absents[cite: 13]
        File projectDirFile = new File(Config.PROJECT_DIR);
        if (!projectDirFile.exists()) {
            projectDirFile.mkdirs();
        }

        File libFile = new File(Config.LIBRAIRIE_PATH);
        if (!libFile.exists()) {
            try {
                LibrairieList librairie = new LibrairieList();
                Gson gson = new Gson();
                String content = gson.toJson(librairie);
                FileWriter writer = new FileWriter(libFile);
                writer.write(content);
                writer.close();
            } catch (Exception e) {
                Log.e(TAG, "Erreur création librairie.json", e);
            }
        }

        // 7. Enregistrement des liaisons JavaScript dans la WebView[cite: 13]
        mgr.registerAll(webView);

        // 8. Chargement de l'interface frontend (depuis les assets Android)
        webView.loadUrl("file:///android_asset/index.html");
    }
}