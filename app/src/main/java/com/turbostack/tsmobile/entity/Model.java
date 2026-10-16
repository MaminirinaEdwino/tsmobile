package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private String nom;
    private List<Champs> attributs;

    public Model() {
        this.attributs = new ArrayList<>();
    }

    public Model(String nom, List<Champs> attributs) {
        this.nom = nom;
        this.attributs = attributs != null ? attributs : new ArrayList<>();
    }

    public ModelJSON toJSON() {
        List<ChampsJSON> attr = new ArrayList<>();
        if (this.attributs != null) {
            for (Champs val : this.attributs) {
                attr.add(val.toJSON());
            }
        }
        return new ModelJSON(this.nom, attr);
    }

    public String getNom() {
        return this.nom;
    }

    public List<Champs> getAttributs() {
        return this.attributs;
    }

    public void setNom(String nom) throws IllegalArgumentException {
        if (nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("Empty value");
        }
        this.nom = nom;
    }

    public void setAttributs(List<Champs> attr) {
        this.attributs = attr;
    }

    public void addAttribut(Champs attr) {
        if (this.attributs == null) {
            this.attributs = new ArrayList<>();
        }
        this.attributs.add(attr);
    }

    public void deleteAttribut(int attrId) {
        if (this.attributs != null && attrId >= 0 && attrId < this.attributs.size()) {
            this.attributs.remove(attrId);
        }
    }
}