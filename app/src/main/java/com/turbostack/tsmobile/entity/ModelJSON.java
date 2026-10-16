package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class ModelJSON {
    public String nom;
    public List<ChampsJSON> champs;

    public ModelJSON() {}

    public ModelJSON(String nom, List<ChampsJSON> champs) {
        this.nom = nom;
        this.champs = champs;
    }

    public Model toModel() {
        List<Champs> attr = new ArrayList<>();
        if (this.champs != null) {
            for (ChampsJSON val : this.champs) {
                attr.add(val.toModel());
            }
        }
        return new Model(this.nom, attr);
    }
}