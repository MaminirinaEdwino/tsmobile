package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class ComposantJSON {
    public String nom;
    public List<PageContentJSON> content;
    public List<Object> params;

    public ComposantJSON() {
        this.content = new ArrayList<>();
        this.params = new ArrayList<>();
    }

    public ComposantJSON(String nom, List<PageContentJSON> content, List<Object> params) {
        this.nom = nom;
        this.content = content;
        this.params = params;
    }

    public Composant toModel() {
        List<PageContent> composant = new ArrayList<>();
        if (this.content != null) {
            for (PageContentJSON val : this.content) {
                composant.add(val.toModel());
            }
        }
        Composant c = new Composant();
        c.setNom(this.nom);
        c.setContenu(composant);
        c.setParams(this.params);
        return c;
    }
}