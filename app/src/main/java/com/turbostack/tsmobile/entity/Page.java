package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private String nom;
    private List<PageContent> contenu;
    private String uri;
    private String styles;
    private List<Asset> asset; // Équivalent de AssetList

    public Page() {
        this.contenu = new ArrayList<>();
        this.asset = new ArrayList<>();
    }

    public PageJSON toJSON() {
        List<PageContentJSON> pcontent = new ArrayList<>();
        for (PageContent val : this.contenu) {
            pcontent.add(val.toJSON());
        }
        return new PageJSON(this.nom, pcontent, this.uri, this.styles, null);
    }

    public String getNom() { return this.nom; }
    public List<PageContent> getContent() { return this.contenu; }
    public String getUri() { return this.uri; }
    public String getStyles() { return this.styles; }

    public Page setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Page setContenu(List<PageContent> contenu) {
        this.contenu = contenu;
        return this;
    }

    public Page setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Page setStyles(String styles) {
        this.styles = styles;
        return this;
    }
}