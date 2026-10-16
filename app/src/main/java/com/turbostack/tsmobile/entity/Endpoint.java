package com.example.app.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Endpoint {
    private String nom;
    private String uri;
    private String method;
    private List<Model> model;
    private List<String> params;
    private String role;
    private List<Model> returnContent;
    private Map<String, String> logic;
    private String returnContentType;
    private String returnPage;
    private String redirectUri;
    private String bodyType;

    public Endpoint() {
        this.model = new ArrayList<>();
        this.params = new ArrayList<>();
        this.returnContent = new ArrayList<>();
        this.logic = new HashMap<>();
    }

    public EndpointJSON toJSON() {
        List<ModelJSON> modelJSONs = new ArrayList<>();
        for (Model val : this.model) {
            modelJSONs.add(val.toJSON());
        }
        List<ModelJSON> returnModelJSONs = new ArrayList<>();
        for (Model val : this.returnContent) {
            returnModelJSONs.add(val.toJSON());
        }
        return new EndpointJSON(
            this.nom, this.uri, this.method, modelJSONs, this.params,
            this.role, this.logic, returnModelJSONs, this.returnContentType,
            this.returnPage, this.redirectUri, this.bodyType
        );
    }

    // Getters et Setters essentiels
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public List<Model> getModel() { return model; }
    public void setModel(List<Model> model) { this.model = model; }

    public List<String> getParams() { return params; }
    public void setParams(List<String> params) { this.params = params; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Map<String, String> getLogic() { return logic; }
    public void setLogic(Map<String, String> logic) { this.logic = logic; }
}