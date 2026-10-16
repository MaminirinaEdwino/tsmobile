package com.example.app.config;

import android.content.Context;
import android.util.Log;
import java.io.File;

public class Config {
    private static final String TAG = "Config";

    // Chemins adaptés pour Android basés sur le répertoire interne de l'application
    public static String TURBO_STACK_DIR;
    public static String PROJECT_DIR;
    public static String LIBRAIRIE_PATH;
    public static String PAGE_LIB_DIR;
    public static String COMPONENT_LIB_DIR;
    public static String STYLE_LIB_DIR;

    // Méthode d'initialisation obligatoire à appeler au démarrage (ex: dans MainActivity.onCreate)
    public static void init(Context context) {
        File internalDir = context.getFilesDir(); // Répertoire de stockage interne de l'application Android
        
        TURBO_STACK_DIR = new File(internalDir, ".turbo_stack").getAbsolutePath();
        PROJECT_DIR = new File(TURBO_STACK_DIR, "turbo_projects").getAbsolutePath();
        LIBRAIRIE_PATH = TURBO_STACK_DIR + "/librairie.json";
        PAGE_LIB_DIR = TURBO_STACK_DIR + "/librairies/page";
        COMPONENT_LIB_DIR = TURBO_STACK_DIR + "/librairies/component";
        STYLE_LIB_DIR = TURBO_STACK_DIR + "/librairies/style";
    }

    // Équivalent de CheckIfExist
    public static boolean checkIfExist(String chemin) {
        File file = new File(chemin);
        if (!file.exists()) {
            return false;
        }
        return file.isDirectory();
    }

    // Équivalent de CheckCreateDir
    public static void checkCreateDir(String path) {
        File filePath = new File(PROJECT_DIR, path);
        if (!checkIfExist(filePath.getAbsolutePath())) {
            filePath.mkdirs(); // Équivalent de os.MkdirAll avec os.ModePerm
        }
    }

    // Équivalent de CheckEmptyFile
    public static boolean checkEmptyFile(String chemin) throws Exception {
        File file = new File(chemin);
        if (!file.exists()) {
            Log.e(TAG, "Le fichier n'existe pas: " + chemin);
            throw new Exception("Le fichier n'existe pas: " + chemin);
        }
        if (file.isDirectory()) {
            throw new Exception(chemin + " est un dossier, pas un fichier");
        }
        return file.length() == 0;
    }
}