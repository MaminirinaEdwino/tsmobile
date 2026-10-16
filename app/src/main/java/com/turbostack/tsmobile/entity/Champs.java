package com.turbostack.tsmobile.entity;

import java.util.List;

public class Champs {
    private String nom;
    private String typeChamps;
    private Object defaultValue;
    private List<Object> constraint;

    public Champs() {}

    public Champs(String nom, String typeChamps, Object defaultValue, List<Object> constraint) {
        this.nom = nom;
        this.typeChamps = typeChamps;
        this.defaultValue = defaultValue;
        this.constraint = constraint;
    }

    public ChampsJSON toJSON() {
        return new ChampsJSON(this.nom, this.typeChamps, this.defaultValue, this.constraint);
    }

    public String getNom() {
        return this.nom;
    }

    public String getType() {
        return this.typeChamps;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public List<Object> getConstraint() {
        return this.constraint;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setType(String typeChamps) {
        this.typeChamps = typeChamps;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setConstraint(List<Object> constraint) {
        this.constraint = constraint;
    }
}