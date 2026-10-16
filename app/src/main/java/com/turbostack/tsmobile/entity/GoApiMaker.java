package com.example.app.entity;

import com.example.app.goapimaker.GoApiMakerUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GoApiMaker {
    private String projectDir;

    public GoApiMaker(String projectDir) {
        this.projectDir = projectDir;
    }

    public void setupFileArch(String name) {
        String projectPath = projectDir + "/" + name + "/api/";
        for (String val : GoApiMakerUtils.DIR_LIST) {
            File f = new File(projectPath + val);
            if (!f.exists()) f.mkdirs();
        }
    }

    public void controllerAPIExporter(List<Endpoint> endpoints, String projectName) {
        if (endpoints == null) return;
        for (Endpoint ep : endpoints) {
            String eName = ep.getNom();
            if (eName == null || eName.isEmpty()) continue;

            String filePath = projectDir + "/" + projectName + "/api/src/controllers/" + eName.replace(" ", "_").toLowerCase() + ".go";
            try (FileWriter file = new FileWriter(filePath)) {
                StringBuilder sb = new StringBuilder();
                // ControllerImportWriter équivalent
                sb.append("package controllers\n\nimport (\n");
                if ("PUT".equalsIgnoreCase(ep.getMethod())) {
                    sb.append("\t\"log\"\n");
                } else if ("POST".equalsIgnoreCase(ep.getMethod())) {
                    sb.append("\t\"fmt\"\n");
                }
                sb.append("\t\"encoding/json\"\n");
                sb.append("\t\"net/http\"\n");
                if (!"DELETE".equalsIgnoreCase(ep.getMethod())) {
                    sb.append(String.format("\t\"%s/src/models\"\n", projectName.replace(" ", "_")));
                }
                sb.append(String.format("\t\"%s/src/config\"\n", projectName.replace(" ", "_")));
                sb.append("\t_ \"github.com/lib/pq\"\n)\n\n");

                String funcName = eName.substring(0, 1).toUpperCase() + eName.substring(1);
                Model activeModel = null;
                if (ep.getModel() != null && !ep.getModel().isEmpty()) {
                    activeModel = ep.getModel().get(0);
                }
                String modelName = activeModel != null ? activeModel.getNom() : "";

                sb.append(String.format("// %s handles the %s request for %s\n", funcName, ep.getMethod(), ep.getUri()));

                if (modelName != null && !modelName.isEmpty()) {
                    String structName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1);
                    String tableName = modelName.toLowerCase();

                    List<String> columns = new java.util.ArrayList<>();
                    List<String> scanTargets = new java.util.ArrayList<>();
                    for (Champs attr : activeModel.getAttributs()) {
                        columns.add(attr.getNom().toLowerCase());
                        scanTargets.add("&res." + attr.getNom().substring(0, 1).toUpperCase() + attr.getNom().substring(1));
                    }

                    String method = ep.getMethod().toUpperCase();
                    switch (method) {
                        case "DELETE":
                            sb.append(deleteHandler(funcName.replace(" ", "_"), "pg", ep.getParams().get(0), tableName));
                            break;
                        case "PUT":
                            sb.append(putHandler(activeModel.getNom(), funcName.replace(" ", "_"), "pg", columns, String.join(", ", scanTargets), ep.getParams().get(0)));
                            break;
                        case "GET":
                            if (ep.getParams() != null && !ep.getParams().isEmpty()) {
                                sb.append(selectByTemplateRender(structName, tableName, ep.getParams().get(0), String.join(", ", scanTargets), funcName.replace(" ", "_")));
                            } else {
                                sb.append(selectTemplate(funcName.replace(" ", "_"), structName, selectQuery(tableName), String.join(", ", scanTargets)));
                            }
                            break;
                        case "POST":
                            List<String> attrs = new java.util.ArrayList<>();
                            for (Champs val : activeModel.getAttributs()) {
                                attrs.add(val.getNom());
                            }
                            sb.append(insertHandler(funcName.replace(" ", "_"), "pg", tableName, attrs));
                            break;
                    }
                } else {
                    sb.append(String.format("\tjson.NewEncoder(w).Encode(map[string]string{\"message\": \"%s endpoint reached\"})\n", eName));
                }

                file.write(sb.toString());
            } catch (IOException e) {
                System.out.println("Error creating controller file: " + e.getMessage());
            }
        }
    }

    private String deleteHandler(String epName, String sgbd, String params, String tableName) {
        return String.format(
            "\nfunc %s(w http.ResponseWriter, r *http.Request) {\n" +
            "\t%s := r.PathValue(\"%s\")\n" +
            "\ttype response struct{ Message string }\n" +
            "\tdb := config.DB\n" +
            "\tdefer db.Close()\n" +
            "\trows, _ := db.Query(\"delete from %s where %s = $1\", %s)\n" +
            "\trows.Next()\n" +
            "\tres := response{Message: \"users deleted\"}\n" +
            "\tw.Header().Set(\"Content-Type\", \"application/json\")\n" +
            "\tw.WriteHeader(http.StatusOK)\n" +
            "\tjson.NewEncoder(w).Encode(res)\n" +
            "}\n", epName, params, params, tableName, params, params
        );
    }

    private String selectTemplate(String handlerName, String responseType, String query, String scanValue) {
        return String.format(
            "\nfunc %s(w http.ResponseWriter, r *http.Request) {\n" +
            "\tdb := config.DB\n\tdefer db.Close()\n" +
            "\tvar res []models.%s\n" +
            "\trows, _ := db.Query(\"%s\")\n" +
            "\tfor rows.Next() {\n" +
            "\t\tvar tmp models.%s\n" +
            "\t\trows.Scan(%s)\n" +
            "\t\tres = append(res, tmp)\n" +
            "\t}\n" +
            "\tw.Header().Set(\"Content-Type\", \"application/json\")\n" +
            "\tw.WriteHeader(http.StatusOK)\n" +
            "\tjson.NewEncoder(w).Encode(res)\n" +
            "}\n", handlerName, responseType, query, responseType, scanValue
        );
    }

    private String selectByTemplateRender(String structName, String tableName, String param, String scanParams, String funcName) {
        return String.format(
            "\nfunc %s(w http.ResponseWriter, r *http.Request) {\n" +
            "\t%s := r.PathValue(\"%s\")\n" +
            "\tvar res models.%s\n" +
            "\tdb := config.DB\n\tdefer db.Close()\n" +
            "\trows, _ := db.Query(\"select * from %s where %s = $1\", %s)\n" +
            "\trows.Next()\n" +
            "\trows.Scan(%s)\n" +
            "\tw.Header().Set(\"Content-Type\", \"application/json\")\n" +
            "\tw.WriteHeader(http.StatusOK)\n" +
            "\tjson.NewEncoder(w).Encode(res)\n" +
            "}\n", funcName, param, param, structName, tableName, param, param, scanParams
        );
    }

    private String insertHandler(String epName, String sgbd, String tableName, List<String> attr) {
        List<String> bodyParams = new java.util.ArrayList<>();
        List<String> placeholders = new java.util.ArrayList<>();
        for (int i = 0; i < attr.size(); i++) {
            String field = attr.get(i);
            bodyParams.add("body." + field.substring(0, 1).toUpperCase() + field.substring(1));
            placeholders.add(String.format("$%d", i + 1));
        }
        String structName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
        return String.format(
            "\nfunc %s(w http.ResponseWriter, r *http.Request) {\n" +
            "\tvar body models.%s\n" +
            "\tjson.NewDecoder(r.Body).Decode(&body)\n" +
            "\tdb := config.DB\n\tdefer db.Close()\n" +
            "\t_, err := db.Exec(\"insert into %s (%s) values (%s)\", %s)\n" +
            "\tif err != nil { log.Fatal(err) }\n" +
            "\tw.Header().Set(\"Content-Type\", \"application/json\")\n" +
            "\tw.WriteHeader(http.StatusOK)\n" +
            "\tjson.NewEncoder(w).Encode(map[string]string{\"status\": \"success\"})\n" +
            "}\n", epName, structName, tableName, String.join(", ", attr), String.join(", ", placeholders), String.join(", ", bodyParams)
        );
    }

    private String putHandler(String modelName, String epName, String sgbd, List<String> attrs, String scanParams, String params) {
        String structName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1);
        List<String> setClauses = new java.util.ArrayList<>();
        for (int i = 0; i < attrs.size(); i++) {
            setClauses.add(String.format("%s = $%d", attrs.get(i), i + 1));
        }
        String query = String.format("update %s set %s where %s = $%d returning *", modelName.toLowerCase(), String.join(", ", setClauses), params, attrs.size() + 1);
        return String.format(
            "\nfunc %s(w http.ResponseWriter, r *http.Request) {\n" +
            "\tvar body models.%s\n" +
            "\t%s := r.PathValue(\"%s\")\n" +
            "\tjson.NewDecoder(r.Body).Decode(&body)\n" +
            "\tdb := config.DB\n\tdefer db.Close()\n" +
            "\trows, err := db.Query(\"%s\", %s)\n" +
            "\tif err != nil { log.Fatal(err) }\n" +
            "\trows.Next()\n" +
            "\tvar res models.%s\n" +
            "\trows.Scan(%s)\n" +
            "\tw.Header().Set(\"Content-Type\", \"application/json\")\n" +
            "\tw.WriteHeader(http.StatusOK)\n" +
            "\tjson.NewEncoder(w).Encode(res)\n" +
            "}\n", epName, structName, params, params, query, params, structName, scanParams
        );
    }

    private String selectQuery(String tableName) {
        return "select * from " + tableName;
    }

    public void modelAPIExporter(List<Model> models, String projectName) {
        if (models == null) return;
        for (Model model : models) {
            String mName = model.getNom();
            if (mName == null || mName.isEmpty()) continue;
            String n = mName.replace(" ", "_").toLowerCase();
            String filePath = projectDir + "/" + projectName + "/api/src/models/" + n + ".go";

            try (FileWriter file = new FileWriter(filePath)) {
                StringBuilder sb = new StringBuilder();
                sb.append("package models\n\n");
                String structName = mName.substring(0, 1).toUpperCase() + mName.substring(1);
                sb.append(String.format("type %s struct {\n", structName));

                for (Champs field : model.getAttributs()) {
                    String fieldName = field.getNom().substring(0, 1).toUpperCase() + field.getNom().substring(1);
                    String goType = GoApiMakerUtils.mapToGoType(field.getType());
                    sb.append(String.format("\t%s %s `json:\"%s\"`\n", fieldName, goType, field.getNom().toLowerCase()));
                }
                sb.append("}\n");
                file.write(sb.toString());
            } catch (IOException e) {
                System.out.println("Error creating model file: " + e.getMessage());
            }
        }
    }

    public void routesAPIExporter(List<Endpoint> endpoints, String projectName) {
        String filePath = projectDir + "/" + projectName + "/api/src/routes/routes.go";
        try (FileWriter file = new FileWriter(filePath)) {
            StringBuilder sb = new StringBuilder();
            sb.append("package routes\n\n");
            sb.append("import (\n");
            sb.append("\t\"net/http\"\n");
            sb.append(String.format("\t\"%s/src/controllers\"\n", projectName.replace(" ", "_")));
            sb.append(")\n\n");

            Map<String, List<Endpoint>> groups = new HashMap<>();
            if (endpoints != null) {
                for (Endpoint ep : endpoints) {
                    String mName = "General";
                    if (ep.getModel() != null && !ep.getModel().isEmpty()) {
                        mName = ep.getModel().get(0).getNom();
                    }
                    groups.computeIfAbsent(mName, k -> new java.util.ArrayList<>()).add(ep);
                }
            }

            for (Map.Entry<String, List<Endpoint>> entry : groups.entrySet()) {
                String groupName = entry.getKey();
                String cleanGroupName = groupName.replace(" ", "_");
                String groupFuncName = cleanGroupName.substring(0, 1).toUpperCase() + cleanGroupName.substring(1);
                sb.append(String.format("func RegisterRoutes%s(mux *http.ServeMux) {\n", groupFuncName));
                for (Endpoint ep : entry.getValue()) {
                    String method = ep.getMethod();
                    String uri = ep.getUri();
                    String eName = ep.getNom();
                    String funcName = eName.substring(0, 1).toUpperCase() + eName.substring(1).replace(" ", "_");
                    sb.append(String.format("\tmux.HandleFunc(\"%s %s\", controllers.%s)\n", method, uri, funcName));
                }
                sb.append("}\n\n");
            }
            file.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error creating routes file: " + e.getMessage());
        }
    }

    public void middlewareAPIExporter(String projectName) {
        GoApiMakerUtils.middlewareWriter(projectName, projectDir);
    }

    public void mainAPIExporter(List<Endpoint> endpoints, String projectName) {
        String filePath = projectDir + "/" + projectName + "/api/main.go";
        try (FileWriter file = new FileWriter(filePath)) {
            StringBuilder sb = new StringBuilder();
            sb.append("package main\n\n");
            sb.append("import (\n");
            sb.append("\t\"fmt\"\n");
            sb.append("\t\"log\"\n");
            sb.append("\t\"net/http\"\n");
            sb.append(String.format("\t\"%s/src/config\"\n", projectName.replace(" ", "_")));
            sb.append(String.format("\t\"%s/src/routes\"\n", projectName.replace(" ", "_")));
            sb.append(String.format("\t\"%s/src/middlewares\"\n", projectName.replace(" ", "_")));
            sb.append(")\n\n");

            sb.append("func main() {\n");
            sb.append("\tconfig.InitDB()\n");
            sb.append("\tmux := http.NewServeMux()\n");

            java.util.Set<String> groups = new java.util.HashSet<>();
            if (endpoints != null) {
                for (Endpoint ep : endpoints) {
                    String mName = "General";
                    if (ep.getModel() != null && !ep.getModel().isEmpty()) {
                        mName = ep.getModel().get(0).getNom();
                    }
                    groups.add(mName);
                }
            }

            for (String groupName : groups) {
                String cleanGroupName = groupName.replace(" ", "_");
                String groupFuncName = cleanGroupName.substring(0, 1).toUpperCase() + cleanGroupName.substring(1);
                sb.append(String.format("\troutes.RegisterRoutes%s(mux)\n", groupFuncName));
            }

            sb.append("\thandler := middlewares.CorsMiddleware(mux)\n");
            sb.append("\tport := \":8080\"\n");
            sb.append("\tfmt.Printf(\"🚀 TurboStack API running on http://localhost%s\\n\", port)\n");
            sb.append("\tlog.Fatal(http.ListenAndServe(port, handler))\n");
            sb.append("}\n");

            file.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error creating main file: " + e.getMessage());
        }
    }

    public void writeModSumFile(String projectName) {
        String modPath = projectDir + "/" + projectName + "/api/go.mod";
        String sumPath = projectDir + "/" + projectName + "/api/go.sum";
        try (FileWriter modFile = new FileWriter(modPath);
             FileWriter sumFile = new FileWriter(sumPath)) {
            modFile.write(GoApiMakerUtils.writeGoMod(projectName.replace(" ", "_")));
            sumFile.write(GoApiMakerUtils.writeSum());
        } catch (IOException e) {
            System.out.println("Error writing mod/sum files: " + e.getMessage());
        }
    }

    public void configAPIExporter(String projectName, List<Model> models) {
        String filePath = projectDir + "/" + projectName + "/api/src/config/db.go";
        Sqlgenerator sqlGen = new Sqlgenerator();
        try (FileWriter file = new FileWriter(filePath)) {
            StringBuilder sb = new StringBuilder();
            sb.append("package config\n\n");
            sb.append("import (\n");
            sb.append("\t\"database/sql\"\n");
            sb.append("\t\"fmt\"\n");
            sb.append("\t\"log\"\n");
            sb.append("\t_ \"github.com/lib/pq\"\n");
            sb.append(")\n\n");
            sb.append("var DB *sql.DB\n\n");
            sb.append("func InitDB() {\n");
            sb.append(String.format("\tconnStr := \"user=postgres password=root dbname=%s sslmode=disable host=localhost port=5432\"\n", projectName.replace(" ", "_").toLowerCase()));
            sb.append("\tvar err error\n");
            sb.append("\tDB, err = sql.Open(\"postgres\", connStr)\n");
            sb.append("\tif err != nil { log.Fatal(err) }\n");
            sb.append("\tif err = DB.Ping(); err != nil { log.Fatal(err) }\n");
            sb.append(String.format("\tquery := `%s`\n", sqlGen.generateDBAndTableInit(models, projectName)));
            sb.append("\t_, err = DB.Exec(query)\n");
            sb.append("\tfmt.Println(\"Successfully connected to database\")\n");
            sb.append("}\n");
            file.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error creating config file: " + e.getMessage());
        }
    }
}