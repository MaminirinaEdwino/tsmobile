package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class PageJSON {
    public String nom;
    public List<PageContentJSON> content;
    public String uri;
    public String styles;
    public List<AssetJSON> assets;

    public PageJSON() {
        this.content = new ArrayList<>();
        this.assets = new ArrayList<>();
    }

    public PageJSON(String nom, List<PageContentJSON> content, String uri, String styles, List<AssetJSON> assets) {
        this.nom = nom;
        this.content = content;
        this.uri = uri;
        this.styles = styles;
        this.assets = assets;
    }

    public Page toModel() {
        List<PageContent> pcontent = new ArrayList<>();
        if (this.content != null) {
            for (PageContentJSON val : this.content) {
                pcontent.add(val.toModel());
            }
        }
        Page p = new Page();
        p.setNom(this.nom);
        p.setContenu(pcontent);
        p.setUri(this.uri);
        p.setStyles(this.styles);
        return p;
    }
}