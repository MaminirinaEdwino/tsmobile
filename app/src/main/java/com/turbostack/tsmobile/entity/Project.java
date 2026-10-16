package com.example.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String nom;
    private String typeProject;
    private String description;
    private BDD bdd;
    private RestApi restApi;
    private WebApp webApp;
    private SiteStatique siteStatique;
    private LocalDateTime updateAt;
    private LocalDateTime createdAt;
    private List<Asset> assets;

    public Project() {
        this.bdd = new BDD();
        this.restApi = new RestApi();
        this.webApp = new WebApp();
        this.siteStatique = new SiteStatique();
        this.assets = new ArrayList<>();
    }

    public ProjectJSON toJSON() {
        List<AssetJSON> assetJSONs = new ArrayList<>();
        for (Asset a : this.assets) {
            assetJSONs.add(a.toJSON());
        }
        return new ProjectJSON(
            this.nom, this.typeProject, this.description,
            this.bdd.toJSON(), this.restApi.toJSON(), this.webApp.toJSON(),
            this.siteStatique.toJSON(), this.updateAt, this.createdAt, assetJSONs
        );
    }

    // Getters
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getType() { return typeProject; }
    public BDD getBDD() { return bdd; }
    public RestApi getRestApi() { return restApi; }
    public WebApp getWebApp() { return webApp; }
    public SiteStatique getSiteStatique() { return siteStatique; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdateAt() { return updateAt; }

    // Setters
    public Project setNom(String nom) { this.nom = nom; return this; }
    public Project setDescription(String description) { this.description = description; return this; }
    public Project setType(String typeProject) { this.typeProject = typeProject; return this; }
    public Project setBDD(BDD bdd) { this.bdd = bdd; return this; }
    public Project setRestApi(RestApi restApi) { this.restApi = restApi; return this; }
    public Project setWebApp(WebApp webApp) { this.webApp = webApp; return this; }
    public Project setSiteStatique(SiteStatique siteStatique) { this.siteStatique = siteStatique; return this; }
    public Project setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
    public Project setUpdateAt(LocalDateTime updateAt) { this.updateAt = updateAt; return this; }
}