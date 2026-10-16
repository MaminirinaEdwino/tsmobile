package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class Composant {
    private String nom;
    private List<PageContent> contenu;
    private List<Object> params;

    public Composant() {
        this.contenu = new ArrayList<>();
        this.params = new ArrayList<>();
    }

    public ComposantJSON toJSON() {
        List<PageContentJSON> composantJSONs = new ArrayList<>();
        for (PageContent val : this.contenu) {
            composantJSONs.add(val.toJSON());
        }
        return new ComposantJSON(this.nom, composantJSONs, this.params);
    }

    public String getNom() { return this.nom; }
    public List<PageContent> getContenu() { return this.contenu; }
    public List<Object> getParams() { return this.params; }

    public Composant setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Composant setContenu(List<PageContent> contenu) {
        this.contenu = contenu;
        return this;
    }

    public Composant setParams(List<Object> params) {
        this.params = params;
        return this;
    }
}