package com.example.app.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.app.config.Config;
import com.example.app.entity.AssetJSON;
import com.example.app.entity.BDDJSON;
import com.example.app.entity.FileNode;
import com.example.app.entity.Project;
import com.example.app.entity.ProjectJSON;
import com.example.app.entity.ProjectManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectService {
    private static final String TAG = "ProjectService";
    private ProjectManager manager;
    private Context context;
    private ProcessManager processManager;

    public ProjectService(Context context, ProjectManager manager) {
        this.context = context;
        this.manager = manager;
        this.processManager = new ProcessManager();
    }

    // Gestionnaire de processus équivalent pour Android
    public static class ProcessManager {
        private final Object lock = new Object();
        private Process currentProcess;
        private boolean isRunning = false;
        private final List<String> logs = new ArrayList<>();
        private int pid = 0;

        public boolean isRunning() { synchronized(lock) { return isRunning; } }
        public List<String> getLogs() { synchronized(lock) { return new ArrayList<>(logs); } }
        public int getPid() { synchronized(lock) { return pid; } }

        public void stop() {
            synchronized(lock) {
                if (currentProcess != null) {
                    currentProcess.destroy();
                    currentProcess = null;
                }
                isRunning = false;
                logs.add("Server Stopped");
            }
        }
    }

    public String getProjectType(String projectName) {
        ProjectJSON project = fetchProjectByName(projectName);
        return project.getType();
    }

    public void deleteProject(String projectName) {
        ProjectJSON project = fetchProjectByName(projectName);
        manager.delete(project.toModel());
    }

    public Map<String, Object> startProject(String projectName) {
        ProjectJSON project = fetchProjectByName(projectName);
        String projectDir = "";
        String type = project.getType() != null ? project.getType() : "";
        
        switch (type) {
            case "static":
                projectDir = Config.PROJECT_DIR + "/" + projectName + "/static";
                manager.exporterStaticSite(project.toModel());
                break;
            case "api":
                projectDir = Config.PROJECT_DIR + "/" + projectName + "/api";
                manager.exporterAPI(project.toModel());
                break;
            case "web_app":
            case "webapp":
                projectDir = Config.PROJECT_DIR + "/" + projectName + "/web_app";
                manager.exporterWebApp(project.toModel());
                break;
        }
        return handleStartProject(projectDir);
    }

    public Map<String, Object> stopProject(String projectName) {
        processManager.stop();
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", processManager.isRunning());
        status.put("logs", processManager.getLogs());
        status.put("pid", processManager.getPid());
        return status;
    }

    public Map<String, Object> statusProject() {
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", processManager.isRunning());
        status.put("logs", processManager.getLogs());
        status.put("pid", processManager.getPid());
        return status;
    }

    public List<String> loadPageLib() {
        List<String> res = new ArrayList<>();
        File dir = new File(Config.PAGE_LIB_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    res.add(content);
                } catch (IOException e) {
                    Log.e(TAG, "Erreur lecture page lib", e);
                }
            }
        }
        return res;
    }

    public List<String> loadComponentLib() {
        List<String> res = new ArrayList<>();
        File dir = new File(Config.COMPONENT_LIB_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    res.add(content);
                } catch (IOException e) {
                    Log.e(TAG, "Erreur lecture component lib", e);
                }
            }
        }
        return res;
    }

    public List<String> loadStyleLib() {
        List<String> res = new ArrayList<>();
        File dir = new File(Config.STYLE_LIB_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    res.add(content);
                } catch (IOException e) {
                    Log.e(TAG, "Erreur lecture style lib", e);
                }
            }
        }
        return res;
    }

    private Map<String, Object> handleStartProject(String projectDir) {
        synchronized(processManager.lock) {
            if (processManager.isRunning) {
                Map<String, Object> status = new HashMap<>();
                status.put("isRunning", processManager.isRunning);
                status.put("logs", processManager.logs);
                status.put("pid", processManager.pid);
                return status;
            }

            processManager.isRunning = true;
            processManager.logs.clear();
            processManager.logs.add("Projet started");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder("sh", "-c", "go run .");
                    pb.directory(new File(projectDir));
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    processManager.currentProcess = process;
                    processManager.pid = 1; // PID simulé sous Android

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        synchronized(processManager.lock) {
                            processManager.logs.add(line);
                        }
                    }
                    process.waitFor();
                } catch (Exception e) {
                    Log.e(TAG, "Erreur exécution processus", e);
                } finally {
                    synchronized(processManager.lock) {
                        processManager.isRunning = false;
                        processManager.logs.add("The project has stopped");
                        processManager.currentProcess = null;
                    }
                }
            });

            Map<String, Object> status = new HashMap<>();
            status.put("isRunning", processManager.isRunning);
            status.put("logs", processManager.logs);
            status.put("pid", processManager.pid);
            return status;
        }
    }

    public String uploadAsset(String projectName, String fileName, String base64file) {
        ProjectJSON project = fetchProjectByName(projectName);
        if (project.getAssets() == null) {
            project.setAssets(new ArrayList<>());
        }
        AssetJSON asset = new AssetJSON();
        asset.setFileName(fileName);
        asset.setBase64Image(base64file);
        project.getAssets().add(asset);
        manager.saveProject(project);
        return "success";
    }

    public String saveScript(String projectName, String scriptName, String script) {
        ProjectJSON project = fetchProjectByName(projectName);
        project.getBdd().addScript(scriptName, script);
        manager.saveProject(project);
        return "success";
    }

    public boolean copyDir(File src, File dst) {
        try {
            if (src.isDirectory()) {
                if (!dst.exists()) {
                    dst.mkdirs();
                }
                String[] children = src.list();
                if (children != null) {
                    for (String child : children) {
                        copyDir(new File(src, child), new File(dst, child));
                    }
                }
            } else {
                Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Erreur copyDir", e);
            return false;
        }
    }

    public String exportProject(String name, String typeProject, String path) {
        Config.checkCreateDir(name);
        ProjectJSON pJson = fetchProjectByName(name);
        switch (typeProject) {
            case "api":
                Config.checkCreateDir(name + "/api");
                manager.exporterAPI(pJson.toModel());
                break;
            case "models":
                Config.checkCreateDir(name + "/models");
                manager.exporterDB(pJson.toModel());
                break;
            case "frontend":
                manager.exporterStaticSite(pJson.toModel());
                break;
            case "full":
            case "webapp":
                Config.checkCreateDir(name + "/webapp");
                manager.exporterWebApp(pJson.toModel());
                break;
        }
        copyDir(new File(Config.PROJECT_DIR, name), new File(path, name));
        return "Success";
    }

    public String createProject(String name, String description, String projectType) {
        ProjectJSON pJson = new ProjectJSON();
        pJson.setNom(name);
        pJson.setDescription(description);
        pJson.setType(projectType);

        if (manager.checkIfExist(name)) {
            return "Project already exists";
        }
        manager.create(pJson.toModel());
        manager.saveProject(pJson);

        switch (pJson.getType()) {
            case "bdd":
                manager.exporterDB(pJson.toModel());
                break;
            case "static":
                manager.exporterStaticSite(pJson.toModel());
                break;
            case "api":
                manager.exporterAPI(pJson.toModel());
                break;
            case "webapp":
            case "web_app":
                manager.exporterWebApp(pJson.toModel());
                break;
        }
        return "Success";
    }

    public String saveProject(String name, String projectJsonStr) {
        try {
            Gson gson = new Gson();
            ProjectJSON pJson = gson.fromJson(projectJsonStr, ProjectJSON.class);
            manager.saveProject(pJson);
            switch (pJson.getType()) {
                case "bdd":
                    manager.exporterDB(pJson.toModel());
                    break;
                case "static":
                    manager.exporterStaticSite(pJson.toModel());
                    break;
                case "api":
                    manager.exporterAPI(pJson.toModel());
                    break;
                case "webapp":
                case "web_app":
                    manager.exporterWebApp(pJson.toModel());
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur saveProject", e);
        }
        return "Success";
    }

    public String saveProjectBDD(String name, String bddJsonStr) {
        try {
            Gson gson = new Gson();
            BDDJSON bdd = gson.fromJson(bddJsonStr, BDDJSON.class);
            ProjectJSON pJson = manager.loadProject(name);
            pJson.setBdd(bdd);
            pJson.setNom(name);
            manager.saveProject(pJson);
        } catch (Exception e) {
            Log.e(TAG, "Erreur saveProjectBDD", e);
        }
        return "Success";
    }

    public List<FileNode> feychProjectFiles(String name) {
        return manager.getProjectFiles(name);
    }

    public String getFileContent(String projectName, String path) {
        try {
            return manager.getFileContent(projectName, path);
        } catch (Exception e) {
            return "Erreur lors de la lecture du fichier : " + e.getMessage();
        }
    }

    public List<ProjectJSON> fetchProjects() {
        List<ProjectJSON> projectList = new ArrayList<>();
        for (Project proj : manager.getProjects()) {
            projectList.add(proj.toJSON());
        }
        return projectList;
    }

    public ProjectJSON fetchProjectByName(String name) {
        File filePath = new File(Config.PROJECT_DIR, name + ".json");
        try {
            byte[] bytes = Files.readAllBytes(filePath.toPath());
            Gson gson = new Gson();
            return gson.fromJson(new String(bytes), ProjectJSON.class);
        } catch (Exception e) {
            Log.e(TAG, "Erreur fetchProjectByName pour " + name, e);
            return new ProjectJSON();
        }
    }

    public List<FileNode> fetchFolderForUpload(String folder) {
        String dirPath;
        List<FileNode> nodes = new ArrayList<>();
        if (folder == null || folder.isEmpty()) {
            // Modification Android : Remplacement de os.UserHomeDir() par le répertoire interne de l'application Android
            dirPath = context.getFilesDir().getAbsolutePath();
        } else {
            dirPath = folder;
        }

        File dir = new File(dirPath);
        File[] entries = dir.listFiles();
        if (entries == null) {
            return new ArrayList<>();
        }

        for (File entry : entries) {
            String name = entry.getName();
            if (entry.isDirectory() && name.startsWith(".") && !name.equals(".turbostack")) {
                continue;
            }
            if (!entry.isDirectory() && name.startsWith(".")) {
                continue;
            }
            if (!entry.isDirectory()) {
                String lowerName = name.toLowerCase();
                if (lowerName.endsWith(".pdf") || lowerName.endsWith(".rar") || lowerName.endsWith(".docx") || 
                    lowerName.endsWith(".doc") || lowerName.endsWith(".txt") || lowerName.endsWith(".ase") || 
                    lowerName.endsWith(".tmp") || lowerName.endsWith(".pixi") || lowerName.endsWith(".odt") || 
                    lowerName.endsWith(".sh") || lowerName.endsWith(".md") || lowerName.endsWith(".db") || 
                    lowerName.endsWith(".amgp") || lowerName.endsWith(".zip") || lowerName.endsWith(".json")) {
                    continue;
                }
            }

            FileNode node = new FileNode();
            node.setName(name);
            node.setPath(entry.getAbsolutePath());
            node.setDir(entry.isDirectory());
            node.setSize(entry.length());
            nodes.add(node);
        }
        return nodes;
    }

    public String getImageAsBase64(String path) {
        try {
            File file = new File(path);
            byte[] bytes = Files.readAllBytes(file.toPath());
            String mimeType = "application/octet-stream";
            String lowerPath = path.toLowerCase();
            if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (lowerPath.endsWith(".png")) {
                mimeType = "image/png";
            } else if (lowerPath.endsWith(".mp4")) {
                mimeType = "video/mp4";
            }

            String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
            return "data:" + mimeType + ";base64," + encoded;
        } catch (Exception e) {
            Log.e(TAG, "Erreur getImageAsBase64", e);
            return "";
        }
    }
}