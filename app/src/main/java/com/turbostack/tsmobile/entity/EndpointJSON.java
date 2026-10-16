package com.example.app.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndpointJSON {
    public String nom;
    public String uri;
    public String method;
    public List<ModelJSON> model;
    public List<String> params;
    public String role;
    public Map<String, String> logic;
    public List<ModelJSON> return_content;
    public String return_content_type;
    public String return_page;
    public String redirect_uri;
    public String body_type;

    public EndpointJSON() {
        this.model = new ArrayList<>();
        this.params = new ArrayList<>();
        this.logic = new HashMap<>();
        this.return_content = new ArrayList<>();
    }

    public EndpointJSON(String nom, String uri, String method, List<ModelJSON> model,
                        List<String> params, String role, Map<String, String> logic,
                        List<ModelJSON> returnContent, String returnContentType,
                        String returnPage, String redirectUri, String bodyType) {
        this.nom = nom;
        this.uri = uri;
        this.method = method;
        this.model = model;
        this.params = params;
        this.role = role;
        this.logic = logic;
        this.return_content = returnContent;
        this.return_content_type = returnContentType;
        this.return_page = returnPage;
        this.redirect_uri = redirectUri;
        this.body_type = bodyType;
    }

    public Endpoint toModel() {
        List<Model> modelList = new ArrayList<>();
        if (this.model != null) {
            for (ModelJSON val : this.model) modelList.add(val.toModel());
        }
        List<Model> returnModelList = new ArrayList<>();
        if (this.return_content != null) {
            for (ModelJSON val : this.return_content) returnModelList.add(val.toModel());
        }

        Endpoint e = new Endpoint();
        e.setNom(this.nom);
        e.setUri(this.uri);
        e.setMethod(this.method);
        e.setModel(modelList);
        e.setParams(this.params);
        e.setRole(this.role);
        e.setLogic(this.logic);
        return e;
    }
}