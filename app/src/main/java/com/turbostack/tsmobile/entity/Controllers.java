package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class ControllerParams {
    private String paramsName;
    private String paramsType;

    public ControllerParams() {}

    public String getParamsName() { return paramsName; }
    public void setParamsName(String paramsName) { this.paramsName = paramsName; }

    public String getParamsType() { return paramsType; }
    public void setParamsType(String paramsType) { this.paramsType = paramsType; }
}

public class Controller {
    private String nom;
    private String pageCible;
    private List<DataBindingJSON> bindings;

    public Controller() {
        this.bindings = new ArrayList<>();
    }

    public ControllerJSON toJSON() {
        return new ControllerJSON(this.nom, this.pageCible, this.bindings);
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPageCible() { return pageCible; }
    public void setPageCible(String pageCible) { this.pageCible = pageCible; }

    public List<DataBindingJSON> getBindings() { return bindings; }
    public void setBindings(List<DataBindingJSON> bindings) { this.bindings = bindings; }
}