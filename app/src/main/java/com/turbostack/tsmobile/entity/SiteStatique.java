package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class SiteStatique {
    private List<Page> pages;
    private List<Composant> composants;

    public SiteStatique() {
        this.pages = new ArrayList<>();
        this.composants = new ArrayList<>();
    }

    public SiteStatiqueJSON toJSON() {
        List<PageJSON> pageJSONs = new ArrayList<>();
        for (Page p : this.pages) pageJSONs.add(p.toJSON());

        List<ComposantJSON> composantJSONs = new ArrayList<>();
        for (Composant c : this.composants) composantJSONs.add(c.toJSON());

        return new SiteStatiqueJSON(pageJSONs, composantJSONs);
    }

    public List<Page> getPages() { return pages; }
    public SiteStatique setPages(List<Page> pages) { this.pages = pages; return this; }

    public List<Composant> getComposants() { return composants; }
    public SiteStatique setComposants(List<Composant> composants) { this.composants = composants; return this; }
}