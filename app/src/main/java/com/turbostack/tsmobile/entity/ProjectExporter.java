package com.example.app.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ProjectExporter {
    private String projectDir;

    public ProjectExporter(String projectDir) {
        this.projectDir = projectDir;
    }

    public void exporterDB(Project project) {
        Sqlgenerator sqlgenerator = new Sqlgenerator();
        BDD db = project.getBDD();
        List<Model> models = db.getModels();
        String projectName = project.getNom();
        
        String schemaSQL = sqlgenerator.generateDBAndTableInit(models, projectName);
        File dir = new File(projectDir + "/" + projectName + "/models/");
        if (!dir.exists()) dir.mkdirs();

        try (FileWriter fw = new FileWriter(dir.getPath() + "/init_db.sql")) {
            fw.write(schemaSQL);
        } catch (IOException e) {
            System.out.println("Erreur création init_db.sql : " + e.getMessage());
        }

        try (FileWriter fw = new FileWriter(dir.getPath() + "/crud_queries.sql")) {
            fw.write("-- CRUD and Join Queries for PostgreSQL\n");
        } catch (IOException e) {
            System.out.println("Erreur création crud_queries.sql : " + e.getMessage());
        }
        System.out.println("Exportation des modèles terminée pour le projet : " + projectName);
    }

    public void exporterAPI(Project project) {
        GoApiMaker apiMaker = new GoApiMaker(projectDir);
        String projectName = project.getNom();
        BDD bdd = project.getBDD();
        apiMaker.setupFileArch(projectName);
        List<Model> models = bdd.getModels();
        RestApi api = project.getRestApi();
        
        apiMaker.modelAPIExporter(models, projectName);
        apiMaker.routesAPIExporter(api.getEndpoints(), projectName);
        apiMaker.configAPIExporter(projectName, models);
        apiMaker.middlewareAPIExporter(projectName);
        apiMaker.mainAPIExporter(api.getEndpoints(), projectName);
        apiMaker.writeModSumFile(projectName);
        System.out.println("Exportation de l'API terminée pour le projet : " + projectName);
    }

    public void exporterStaticSite(Project project) {
        String projectName = project.getNom();
        SiteStatique site = project.getSiteStatique();
        Staticsitemaker ssm = new Staticsitemaker(projectDir);
        ssm.setupStaticArch(projectName);

        File viewsDir = new File(projectDir + "/" + projectName + "/static/views/");
        if (!viewsDir.exists()) viewsDir.mkdirs();

        if (site.getPages() != null) {
            for (Page page : site.getPages()) {
                String pageName = page.getNom().toLowerCase().replace(" ", "_");
                File viewFile = new File(viewsDir, pageName + ".html");
                try (FileWriter fw = new FileWriter(viewFile)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
                    sb.append("\t<meta charset=\"UTF-8\">\n");
                    sb.append(String.format("\t<title>%s</title>\n", page.getNom()));
                    sb.append("\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
                    sb.append(String.format("\t<link rel=\"stylesheet\" href=\"/static/css/%s.css\">\n", pageName));
                    sb.append("</head>\n<body>\n");
                    sb.append(ssm.renderBlocksToHTML(page.getContent(), projectName, pageName));
                    sb.append("\n</body>\n</html>");
                    fw.write(sb.toString());
                } catch (IOException e) {
                    System.out.println("Erreur écriture page statique : " + e.getMessage());
                }
            }
        }

        String mainPath = projectDir + "/" + projectName + "/static/main.go";
        String modPath = projectDir + "/" + projectName + "/static/go.mod";
        ssm.setupGoServerCode(site.getPages(), mainPath);
        try (FileWriter fw = new FileWriter(modPath)) {
            fw.write(ssm.writeGoMod(projectName));
        } catch (IOException e) {
            System.out.println("Erreur go.mod site statique : " + e.getMessage());
        }
        System.out.println("Exportation du site statique terminée : " + projectName);
    }

    public void exporterWebApp(Project project) {
        WebAppMaker webAppMaker = new WebAppMaker(
            project.getNom(),
            project.getWebApp(),
            "go",
            project.getRestApi(),
            project.getBDD(),
            projectDir
        );
        webAppMaker.webAppGenerator();
    }
}