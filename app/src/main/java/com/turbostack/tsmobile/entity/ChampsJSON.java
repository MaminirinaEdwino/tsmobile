package com.example.app.entity;

import java.util.List;

public class ChampsJSON {
    public String nom;
    public String type;
    public Object default_value;
    public List<Object> constraint;

    public ChampsJSON() {}

    public ChampsJSON(String nom, String type, Object defaultValue, List<Object> constraint) {
        this.nom = nom;
        this.type = type;
        this.default_value = defaultValue;
        this.constraint = constraint;
    }

    public Champs toModel() {
        return new Champs(this.nom, this.type, this.default_value, this.constraint);
    }
}