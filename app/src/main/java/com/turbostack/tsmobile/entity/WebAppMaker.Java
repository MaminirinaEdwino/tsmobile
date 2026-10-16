package com.example.app.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WebAppMaker {
    private String projectName;
    private WebApp webApp;
    private String techno;
    private RestApi api;
    private BDD bdd;
    private String projectDir;

    public WebAppMaker(String projectName, WebApp webApp, String techno, RestApi api, BDD bdd, String projectDir) {
        this.projectName = projectName;
        this.webApp = webApp;
        this.techno = techno;
        this.api = api;
        this.bdd = bdd;
        this.projectDir = projectDir;
    }

    public void setupArch() {
        String projectPath = projectDir + "/" + projectName + "/web-app/";
        String[] folderList = {
            "src/views", "src/controller", "src/models",
            "src/static", "src/router", "src/config",
            "src/static/css", "src/static/assets"
        };
        for (String dir : folderList) {
            new File(projectPath + dir).mkdirs();
        }
    }

    public void createConfigFile() {
        if ("go".equals(techno)) {
            String filePath = projectDir + "/" + projectName + "/web-app/src/config/db.go";
            Sqlgenerator sqlgen = new Sqlgenerator();
            try (FileWriter file = new FileWriter(filePath)) {
                StringBuilder sb = new StringBuilder();
                sb.append("package config\n\n");
                sb.append("import (\n\t\"database/sql\"\n\t\"fmt\"\n\t\"log\"\n\t_ \"github.com/lib/pq\"\n)\n\n");
                sb.append("var DB *sql.DB\n\n");
                sb.append(String.format("var connStr = \"user=postgres password=root dbname=%s sslmode=disable host=localhost port=5432\"\n", projectName.replace(" ", "_").toLowerCase()));
                sb.append("func ConnectDB() *sql.DB {\n\tvar err error\n\tDB, err = sql.Open(\"postgres\", connStr)\n\tif err != nil { log.Fatal(err) }\n\treturn DB\n}\n");
                sb.append("func InitDB() {\n");
                sb.append(String.format("\tquery := `%s`\n", sqlgen.generateDBAndTableInit(bdd.getModels(), projectName)));
                sb.append("\t_, err := DB.Exec(query)\n\tif err != nil { fmt.Errorf(\"échec: %w\", err) }\n");
                sb.append("\tfmt.Println(\"Successfully connected to database\")\n}\n");
                file.write(sb.toString());
            } catch (IOException e) {
                System.out.println("Error creating webapp config: " + e.getMessage());
            }
        }
    }

    public String webAppSelectTemplate(String query, String dbCaller, String returnType, String scanValue, String pageName, String modelName) {
        return String.format(
            "func(w http.ResponseWriter, r *http.Request) {\n" +
            "\t%s\n\t%s\n" +
            "\trows, err := db.Query(\"%s\")\n" +
            "\tif err != nil { http.Error(w, \"Erreur BDD: \"+err.Error(), http.StatusInternalServerError); return }\n" +
            "\tdefer rows.Close()\n" +
            "\tvar returnValues []%s\n" +
            "\tfor rows.Next() {\n" +
            "\t\tvar returnValue %s\n" +
            "\t\tif err := rows.Scan(%s); err != nil { continue }\n" +
            "\t\treturnValues = append(returnValues, returnValue)\n" +
            "\t}\n" +
            "\trenderTemplate(w, \"%s.html\", map[string]interface{}{\"%s\": returnValues})\n" +
            "}", dbCaller, query, scanValue, returnType, returnType, scanValue, pageName, modelName
        );
    }

    public String webAppPostActionTemplate(String dbCaller, String redirectUri, String paramsExtraction, String paramsChecker, String query, String paramsExec) {
        return String.format(
            "func(w http.ResponseWriter, r *http.Request) {\n" +
            "\t%s\n" +
            "\tr.ParseMultipartForm(32 << 20);\n" +
            "\t%s\n" +
            "\tif %s {\n\t\thttp.Error(w, \"Champs requis\", http.StatusBadRequest); return\n\t}\n" +
            "\t_, err := db.Exec(\"%s\", %s)\n" +
            "\tif err != nil { http.Error(w, \"Erreur création: \"+err.Error(), http.StatusInternalServerError); return }\n" +
            "\thttp.Redirect(w, r, \"%s\", http.StatusSeeOther)\n" +
            "}", dbCaller, paramsExtraction, paramsChecker, query, paramsExec, redirectUri
        );
    }

    public String handleURIParamsSyntaxeForGo(String uri) {
        String[] uriTab = uri.split("/");
        List<String> realUri = new java.util.ArrayList<>();
        for (String val : uriTab) {
            String res = val;
            if (val.contains(":")) {
                res = String.format("{%s}", val.replace(":", ""));
            }
            realUri.add(res);
        }
        return String.join("/", realUri);
    }

    public String writeReturnType(Endpoint endpoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("struct{\n");
        if (endpoint.getReturnContent() != null) {
            for (Model val : endpoint.getReturnContent()) {
                for (Champs field : val.getAttributs()) {
                    sb.append(String.format("\t%s %s\n", field.getNom().substring(0, 1).toUpperCase() + field.getNom().substring(1), field.getType()));
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public void createControllerFile() {
        if ("go".equals(techno)) {
            String filePath = projectDir + "/" + projectName + "/web-app/src/controller/controller.go";
            try (FileWriter writer = new FileWriter(filePath)) {
                StringBuilder sb = new StringBuilder();
                sb.append("package controller\n\n");
                sb.append("import (\n\t\"net/http\"\n\t\"text/template\"\n\t\"");
                sb.append(projectName.replace(" ", "_"));
                sb.append("/src/config\"\n)\n");

                sb.append(pageRenderer());
                sb.append("func RegisterRoutes(mux *http.ServeMux) {\n");
                if (api != null && api.getEndpoints() != null) {
                    for (Endpoint ep : api.getEndpoints()) {
                        sb.append(String.format("\t// Route: %s %s\n", ep.getMethod(), ep.getUri()));
                    }
                }
                sb.append("}\n");
                writer.write(sb.toString());
            } catch (IOException e) {
                System.out.println("Error creating webapp controller: " + e.getMessage());
            }
        }
    }

    public void mainExporter() {
        String filePath = projectDir + "/" + projectName + "/web-app/main.go";
        try (FileWriter file = new FileWriter(filePath)) {
            StringBuilder sb = new StringBuilder();
            sb.append("package main\n\n");
            sb.append("import (\n");
            sb.append("\t\"fmt\"\n\t\"log\"\n\t\"net/http\"\n");
            sb.append(String.format("\t\"%s/src/config\"\n", projectName.replace(" ", "_")));
            sb.append(String.format("\t\"%s/src/controller\"\n", projectName.replace(" ", "_")));
            sb.append(")\n\n");
            sb.append("func main() {\n");
            sb.append("\tconfig.InitDB()\n");
            sb.append("\tmux := http.NewServeMux()\n");
            sb.append("\tcontroller.RegisterRoutes(mux)\n");
            sb.append("\tstaticServer := http.FileServer(http.Dir(\"src/static\"))\n");
            sb.append("\tmux.Handle(\"GET /static/\", http.StripPrefix(\"/static/\", staticServer))\n");
            sb.append("\tfmt.Printf(\"🚀 WebApp running on http://localhost:8080\\n\")\n");
            sb.append("\tlog.Fatal(http.ListenAndServe(\":8080\", mux))\n");
            sb.append("}\n");
            file.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error creating webapp main.go: " + e.getMessage());
        }
    }

    public void webAppGenerator() {
        setupArch();
        createConfigFile();
    }
}