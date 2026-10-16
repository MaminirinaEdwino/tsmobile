package com.example.app.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDDJSON {
    public List<ModelJSON> models;
    public String sgbd;
    public List<Map<String, String>> scripts;

    public BDDJSON() {
        this.models = new ArrayList<>();
        this.scripts = new ArrayList<>();
    }

    public BDDJSON(List<ModelJSON> models, String sgbd, List<Map<String, String>> scripts) {
        this.models = models;
        this.sgbd = sgbd;
        this.scripts = scripts;
    }

    public String addScript(String scriptName, String script) {
        Map<String, String> map = new HashMap<>();
        map.put("scriptName", scriptName);
        map.put("script", script);
        this.scripts.add(map);
        return "success";
    }

    public BDD toModel() {
        List<Model> dbModel = new ArrayList<>();
        if (this.models != null) {
            for (ModelJSON val : this.models) {
                dbModel.add(val.toModel());
            }
        }
        return new BDD(dbModel, this.scripts, this.sgbd);
    }
}