package com.example.app.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDD {
    private List<Model> models;
    private List<Map<String, String>> scripts;
    private String sgbd;

    public BDD() {
        this.models = new ArrayList<>();
        this.scripts = new ArrayList<>();
    }

    public BDD(List<Model> models, List<Map<String, String>> scripts, String sgbd) {
        this.models = models != null ? models : new ArrayList<>();
        this.scripts = scripts != null ? scripts : new ArrayList<>();
        this.sgbd = sgbd;
    }

    public BDDJSON toJSON() {
        List<ModelJSON> dbModel = new ArrayList<>();
        for (Model val : this.models) {
            dbModel.add(val.toJSON());
        }
        return new BDDJSON(dbModel, this.sgbd, this.scripts);
    }

    public List<Model> getModels() {
        return this.models;
    }

    public String getSgbd() {
        return this.sgbd;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public void setSgbd(String sgbd) {
        this.sgbd = sgbd;
    }

    public List<Model> addModel(Model model) {
        this.models.add(model);
        return this.models;
    }

    public List<Model> deleteModel(int modelId) {
        if (modelId >= 0 && modelId < this.models.size()) {
            this.models.remove(modelId);
        }
        return this.models;
    }
}