package com.example.app.entity;

import com.google.gson.Gson; // Utilisation classique de Gson sous Android/Java pour parser le JSON de styles
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Staticsitemaker {
    private String projectDir;

    public Staticsitemaker(String projectDir) {
        this.projectDir = projectDir;
    }

    public void setupStaticArch(String name) {
        String projectPath = projectDir + "/" + name + "/static/";
        File dir = new File(projectPath);
        if (dir.exists()) {
            deleteDir(dir);
        }
        String[] dirList = {
            "static/css",
            "static/js",
            "static/assets",
            "views"
        };
        for (String val : dirList) {
            new File(projectPath + val).mkdirs();
        }
    }

    private void deleteDir(File f) {
        File[] contents = f.listFiles();
        if (contents != null) {
            for (File c : contents) {
                deleteDir(c);
            }
        }
        f.delete();
    }

    public String writeGoMod(String projectName) {
        return String.format("\nmodule %s\n\ngo 1.25.6\n", projectName.replace(" ", "_"));
    }

    public void setupGoServerCode(List<Page> pages, String filePath) {
        try (FileWriter mainFile = new FileWriter(filePath)) {
            mainFile.write("package main\n\nimport (\n\t\"net/http\"\n\t\"fmt\"\n)\n\n");

            if (pages != null) {
                for (Page page : pages) {
                    String pageName = page.getNom().toLowerCase().replace(" ", "_");
                    mainFile.write(String.format("func handle_%s(w http.ResponseWriter, r *http.Request) {\n\thttp.ServeFile(w, r, \"views/%s.html\")\n}\n\n", pageName, pageName));
                }
            }

            String startServer = "\nfunc startServer(mux *http.ServeMux, PORT int) {\n" +
                                 "    fmt.Printf(\"Server started on port : http://localhost:%d\\n\", PORT)\n" +
                                 "    err := http.ListenAndServe(fmt.Sprintf(\":%d\", PORT), mux)\n" +
                                 "    if err != nil {\n        fmt.Println(err)\n        startServer(mux, PORT+1)\n    }\n}\n";
            mainFile.write(startServer);
            mainFile.write("func main() {\n");
            mainFile.write("\tmux := http.NewServeMux()\n");
            mainFile.write("\tstaticServer := http.FileServer(http.Dir(\"static\"))\n");
            mainFile.write("\tPORT := 8080\n");
            mainFile.write("\tmux.Handle(\"GET /static/\", http.StripPrefix(\"/static/\", staticServer))\n");

            if (pages != null) {
                for (Page page : pages) {
                    String pageName = page.getNom().toLowerCase().replace(" ", "_");
                    mainFile.write(String.format("\tmux.HandleFunc(\"GET %s\", handle_%s)\n", page.getUri(), pageName));
                }
            }
            mainFile.write("\tstartServer(mux, PORT)\n}\n");
        } catch (IOException e) {
            System.out.println("Error writing Go server code: " + e.getMessage());
        }
    }

    private void checkValueSb(StringBuilder sb, String key, String value) {
        if (value != null && !value.isEmpty()) {
            sb.append(String.format("\t%s:%s;\n", key, value));
        }
    }

    private void checkValueFile(FileWriter fw, String key, String value) {
        if (value != null && !value.isEmpty()) {
            try {
                fw.write(String.format("\t%s:%s;\n", key, value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void styleWriter(Page page, File cssFile) {
        StringBuilder mobileSb = new StringBuilder();
        StringBuilder tabletSb = new StringBuilder();
        String style = page.getStyles();

        if (style != null && !style.isEmpty()) {
            try {
                Gson gson = new Gson();
                Map<String, Map<String, String>> cssVal = gson.fromJson(style, Map.class);

                Map<String, String> desktop = cssVal != null ? cssVal.get("desktop") : null;
                Map<String, String> tablet = cssVal != null ? cssVal.get("tablet") : null;
                Map<String, String> mobile = cssVal != null ? cssVal.get("mobile") : null;

                try (FileWriter fw = new FileWriter(cssFile, true)) {
                    if (tablet != null && !tablet.isEmpty()) {
                        for (Map.Entry<String, String> entry : tablet.entrySet()) {
                            tabletSb.append(entry.getKey()).append(" {\n");
                            tabletSb.append(entry.getValue()).append(";\n}\n");
                        }
                    }

                    if (mobile != null && !mobile.isEmpty()) {
                        for (Map.Entry<String, String> entry : mobile.entrySet()) {
                            mobileSb.append(entry.getKey()).append(" {\n");
                            mobileSb.append(entry.getValue()).append(";\n}\n");
                        }
                    }

                    if (desktop != null && !desktop.isEmpty()) {
                        for (Map.Entry<String, String> entry : desktop.entrySet()) {
                            fw.write(entry.getKey() + " {\n");
                            fw.write(entry.getValue() + ";\n}\n");
                        }
                    }

                    if (mobileSb.length() > 0) {
                        fw.write("\n\n@media (max-width: 375px) {\n");
                        fw.write(mobileSb.toString());
                        fw.write("}\n");
                    }

                    if (tabletSb.length() > 0) {
                        fw.write("@media (min-width: 376px) and (max-width: 1024px) {\n");
                        fw.write(tabletSb.toString());
                        fw.write("}\n");
                    }
                }
            } catch (Exception e) {
                System.out.println("error styleWriter: " + e.getMessage());
            }
        }
    }

    public String renderBlocksToHTML(List<PageContent> blocks, String projectName, String pageName) {
        if (blocks == null) return "";
        String cssPath = projectDir + "/" + projectName + "/static/static/css/" + pageName + ".css";

        StringBuilder sb = new StringBuilder();
        StringBuilder desktopSb = new StringBuilder();
        StringBuilder tabletSb = new StringBuilder();

        try (FileWriter cssFile = new FileWriter(cssPath, true)) {
            for (PageContent block : blocks) {
                String tag = block.getTag();
                String content = block.getContent();
                String className = block.getClassName();
                String style = block.getStyles();
                String id = block.getId();
                String inputType = block.getInputType();
                String placeholder = block.getPlaceholder();

                if ("img".equals(tag)) {
                    sb.append(String.format("<img src=\"%s\" class=\"%s\" data-block-id=\"%s\" />", content, className, id));
                    continue;
                } else if ("input".equals(tag)) {
                    sb.append(String.format("<input type=\"%s\" class=\"%s\" data-block-id=\"%s\" placeholder=\"%s\"/>", inputType, className, id, placeholder));
                    continue;
                }

                sb.append(String.format("<%s class=\"%s\" data-block-id=\"%s\" > ", tag, className, id));

                if (style != null && !style.isEmpty()) {
                    try {
                        Gson gson = new Gson();
                        Map<String, Map<String, String>> cssVal = gson.fromJson(style, Map.class);
                        Map<String, String> desktop = cssVal != null ? cssVal.get("desktop") : null;
                        Map<String, String> tablet = cssVal != null ? cssVal.get("tablet") : null;
                        Map<String, String> mobile = cssVal != null ? cssVal.get("mobile") : null;

                        if (tablet != null && !tablet.isEmpty()) {
                            tabletSb.append(String.format("[data-block-id=\"%s\"]{\n", id));
                            for (Map.Entry<String, String> entry : tablet.entrySet()) {
                                checkValueSb(tabletSb, entry.getKey(), entry.getValue());
                            }
                            tabletSb.append("}\n");
                        }

                        if (mobile != null && !mobile.isEmpty()) {
                            desktopSb.append(String.format("[data-block-id=\"%s\"]{\n", id));
                            for (Map.Entry<String, String> entry : mobile.entrySet()) {
                                checkValueSb(desktopSb, entry.getKey(), entry.getValue());
                            }
                            desktopSb.append("}\n");
                        }

                        if (desktop != null && !desktop.isEmpty()) {
                            cssFile.write(String.format("[data-block-id=\"%s\"]{\n", id));
                            for (Map.Entry<String, String> entry : desktop.entrySet()) {
                                checkValueFile(cssFile, entry.getKey(), entry.getValue());
                            }
                            cssFile.write("}\n");
                        }
                    } catch (Exception e) {
                        System.out.println("error block style: " + e.getMessage());
                    }
                }

                if (content != null) {
                    sb.append(content);
                }

                if (block.getChildren() != null && !block.getChildren().isEmpty()) {
                    sb.append(renderBlocksToHTML(block.getChildren(), projectName, pageName));
                }

                sb.append(String.format("</%s>", tag));
            }

            if (desktopSb.length() > 0) {
                cssFile.write("@media (max-width: 375px) {\n");
                cssFile.write(desktopSb.toString());
                cssFile.write("}\n");
            }
            if (tabletSb.length() > 0) {
                cssFile.write("@media (min-width: 376px) and (max-width: 1024px) {\n");
                cssFile.write(tabletSb.toString());
                cssFile.write("}\n");
            }
        } catch (IOException e) {
            System.out.println("Error rendering blocks to HTML: " + e.getMessage());
        }

        return sb.toString();
    }
}