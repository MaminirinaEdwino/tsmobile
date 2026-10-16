package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class WebApp {
    private List<Page> pages;
    private List<String> role;
    private List<Composant> composant;
    private List<Controller> controllers;
    private BDD bdd;

    public WebApp() {
        this.pages = new ArrayList<>();
        this.role = new ArrayList<>();
        this.composant = new ArrayList<>();
        this.controllers = new ArrayList<>();
        this.bdd = new BDD();
    }

    public WebAppJSON toJSON() {
        List<PageJSON> pageJSONs = new ArrayList<>();
        for (Page p : this.pages) pageJSONs.add(p.toJSON());

        List<ComposantJSON> compJSONs = new ArrayList<>();
        for (Composant c : this.composant) compJSONs.add(c.toJSON());

        List<ControllerJSON> ctrlJSONs = new ArrayList<>();
        for (Controller ctrl : this.controllers) ctrlJSONs.add(ctrl.toJSON());

        return new WebAppJSON(pageJSONs, this.role, compJSONs, this.bdd.toJSON(), ctrlJSONs);
    }

    public List<DataBindingJSON> getBindingsForPage(String pageNom) {
        List<DataBindingJSON> activeBindings = new ArrayList<>();
        for (Controller ctrl : this.controllers) {
            if (ctrl.getPageCible().equals(pageNom)) {
                activeBindings.addAll(ctrl.getBindings());
            }
        }
        return activeBindings;
    }

    // Getters et Setters
    public List<Page> getPages() { return pages; }
    public WebApp setPages(List<Page> pages) { this.pages = pages; return this; }

    public List<String> getRole() { return role; }
    public WebApp setRole(List<String> role) { this.role = role; return this; }

    public List<Composant> getComposant() { return composant; }
    public WebApp setComposant(List<Composant> composant) { this.composant = composant; return this; }

    public List<Controller> getControllers() { return controllers; }
    public WebApp setControllers(List<Controller> controllers) { this.controllers = controllers; return this; }

    public BDD getBdd() { return bdd; }
    public WebApp setBdd(BDD bdd) { this.bdd = bdd; return this; }
}