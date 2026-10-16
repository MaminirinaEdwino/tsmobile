package com.example.app.goapimaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GoApiMakerUtils {

    public static final String[] DIR_LIST = {
        "src/config",
        "src/controllers",
        "src/middlewares",
        "src/routes",
        "src/models"
    };

    public static String mapToGoType(String uiType) {
        if (uiType == null) return "string";
        switch (uiType.toLowerCase()) {
            case "int":
                return "int";
            case "string":
            case "text":
                return "string";
            default:
                return "string";
        }
    }

    public static String dbCallerTemplate() {
        return "\ndb := config.DB\ndefer db.Close()\n";
    }

    public static String dbCallerTemplateWebApp() {
        return "\ndb := config.ConnectDB()\ndefine db.Close()\n";
    }

    public static String writeResponseWriter() {
        return "\nw.Header().Set(\"Content-Type\", \"application/json\")\n" +
               "w.WriteHeader(http.StatusOK)\n" +
               "json.NewEncoder(w).Encode(res)\n";
    }

    // Générateurs de requêtes SQL
    public static String delete(String table, String params) {
        return String.format("delete from %s where %s = $1", table, params);
    }

    public static String update(String tableName, List<String> attrs, String params) {
        return String.format("update %s set %s where %s = $%d returning * ", tableName, String.join(", ", attrs), params, attrs.size() + 1);
    }

    public static String select(String tableName) {
        return String.format("select * from %s", tableName);
    }

    public static String selectWithAttr(String tableName, String attr) {
        return String.format("select %s from %s", attr, tableName);
    }

    public static String selectBy(String tableName, String params) {
        return String.format("select * from %s where %s = $1", params, tableName);
    }

    public static String selectByWithAttr(String tableName, String params, String paramsGet) {
        return String.format("select %s from %s where %s = $1", params, tableName, paramsGet);
    }

    public static String insert(String tableName, List<String> attr) {
        List<String> tmp = new java.util.ArrayList<>();
        for (int i = 0; i < attr.size(); i++) {
            tmp.add(String.format("$%d", i + 1));
        }
        return String.format("insert into %s (%s) values (%s)", tableName, String.join(", ", attr), String.join(", ", tmp));
    }

    public static String writeGoMod(String projectName) {
        return String.format("\nmodule %s\n\ngo 1.25.6\n\nrequire github.com/lib/pq v1.12.3 // indirect\n", projectName);
    }

    public static String writeSum() {
        return "\ngithub.com/lib/pq v1.12.3 h1:tTWxr2YLKwIvK90ZXEw8GP7UFHtcbTtty8zsI+YjrfQ=\n" +
               "github.com/lib/pq v1.12.3/go.mod h1:/p+8NSbOcwzAEI7wiMXFlgydTwcgTr3OSKMsD2BitpA=\n";
    }

    public static void middlewareWriter(String projectName, String projectDir) {
        String filePath = projectDir + "/" + projectName + "/api/src/middlewares/cors.go";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("package middlewares\n\n");
            writer.write("import \"net/http\"\n\n");
            writer.write("func CorsMiddleware(next http.Handler) http.Handler {\n");
            writer.write("\treturn http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {\n");
            writer.write("\t\tw.Header().Set(\"Access-Control-Allow-Origin\", \"*\")\n");
            writer.write("\t\tw.Header().Set(\"Access-Control-Allow-Methods\", \"GET, POST, PUT, DELETE, OPTIONS\")\n");
            writer.write("\t\tw.Header().Set(\"Access-Control-Allow-Headers\", \"Content-Type, Authorization\")\n\n");
            writer.write("\t\tif r.Method == \"OPTIONS\" {\n");
            writer.write("\t\t\tw.WriteHeader(http.StatusOK)\n");
            writer.write("\t\t\treturn\n");
            writer.write("\t\t}\n\n");
            writer.write("\t\tnext.ServeHTTP(w, r)\n");
            writer.write("\t})\n");
            writer.write("}\n");
        } catch (IOException e) {
            System.out.println("Error creating middleware file: " + e.getMessage());
        }
    }
}