package com.example.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RestApiJSON {
    public List<EndpointJSON> endpoints;
    public List<String> role;
    public BDDJSON bdd;

    public RestApiJSON() {
        this.endpoints = new ArrayList<>();
        this.role = new ArrayList<>();
        this.bdd = new BDDJSON();
    }

    public RestApiJSON(List<EndpointJSON> endpoints, List<String> role, BDDJSON bdd) {
        this.endpoints = endpoints;
        this.role = role;
        this.bdd = bdd;
    }

    public RestApi toModel() {
        List<Endpoint> eps = new ArrayList<>();
        if (this.endpoints != null) {
            for (EndpointJSON val : this.endpoints) eps.add(val.toModel());
        }
        RestApi ra = new RestApi();
        ra.setEndpoints(eps);
        ra.setRole(this.role);
        ra.setBdd(this.bdd.toModel());
        return ra;
    }
}

public class SiteStatiqueJSON {
    public List<PageJSON> pages;
    public List<ComposantJSON> composants;

    public SiteStatiqueJSON() {
        this.pages = new ArrayList<>();
        this.composants = new ArrayList<>();
    }

    public SiteStatiqueJSON(List<PageJSON> pages, List<ComposantJSON> composants) {
        this.pages = pages;
        this.composants = composants;
    }

    public SiteStatique toModel() {
        List<Page> pList = new ArrayList<>();
        if (this.pages != null) for (PageJSON val : this.pages) pList.add(val.toModel());
        List<Composant> cList = new ArrayList<>();
        if (this.composants != null) for (ComposantJSON val : this.composants) cList.add(val.toModel());

        SiteStatique ss = new SiteStatique();
        ss.setPages(pList);
        ss.setComposants(cList);
        return ss;
    }
}

public class DataBindingJSON {
    public String id_element;
    public String endpoint_nom;
    public String trigger;
    public String action;
    public String map_field;
}

public class ControllerJSON {
    public String nom;
    public String page_nom;
    public List<DataBindingJSON> bindings;

    public ControllerJSON() {
        this.bindings = new ArrayList<>();
    }

    public ControllerJSON(String nom, String pageNom, List<DataBindingJSON> bindings) {
        this.nom = nom;
        this.page_nom = pageNom;
        this.bindings = bindings;
    }
}

public class WebAppJSON {
    public List<PageJSON> pages;
    public List<String> role;
    public List<ComposantJSON> composant;
    public BDDJSON bdd;
    public List<ControllerJSON> controllers;

    public WebAppJSON() {
        this.pages = new ArrayList<>();
        this.role = new ArrayList<>();
        this.composant = new ArrayList<>();
        this.controllers = new ArrayList<>();
        this.bdd = new BDDJSON();
    }

    public WebAppJSON(List<PageJSON> pages, List<String> role, List<ComposantJSON> composant, BDDJSON bdd, List<ControllerJSON> controllers) {
        this.pages = pages;
        this.role = role;
        this.composant = composant;
        this.bdd = bdd;
        this.controllers = controllers;
    }
}

public class ProjectJSON {
    public String nom;
    public String type;
    public BDDJSON bdd;
    public RestApiJSON rest_api;
    public WebAppJSON web_app;
    public SiteStatiqueJSON site_statique;
    public String description;
    public LocalDateTime update_at;
    public LocalDateTime created_at;
    public List<AssetJSON> assets;

    public ProjectJSON() {
        this.bdd = new BDDJSON();
        this.rest_api = new RestApiJSON();
        this.web_app = new WebAppJSON();
        this.site_statique = new SiteStatiqueJSON();
        this.assets = new ArrayList<>();
    }

    public ProjectJSON(String nom, String type, String description, BDDJSON bdd,
                         RestApiJSON restApi, WebAppJSON webApp, SiteStatiqueJSON siteStatique,
                         LocalDateTime updateAt, LocalDateTime createdAt, List<AssetJSON> assets) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.bdd = bdd;
        this.rest_api = restApi;
        this.web_app = webApp;
        this.site_statique = siteStatique;
        this.update_at = updateAt;
        this.created_at = createdAt;
        this.assets = assets;
    }

    public Project toModel() {
        List<Asset> assetList = new ArrayList<>();
        if (this.assets != null) {
            for (AssetJSON aj : this.assets) assetList.add(aj.toModel());
        }
        Project p = new Project();
        p.setNom(this.nom);
        p.setType(this.type);
        p.setDescription(this.description);
        p.setBDD(this.bdd != null ? this.bdd.toModel() : new BDD());
        p.setRestApi(this.rest_api != null ? this.rest_api.toModel() : new RestApi());
        p.setWebApp(new WebApp());
        p.setSiteStatique(this.site_statique != null ? this.site_statique.toModel() : new SiteStatique());
        p.setCreatedAt(this.created_at);
        p.setUpdateAt(this.update_at);
        return p;
    }
}