package com.turbostack.tsmobile.entity;

import java.time.LocalDateTime;

public class Abonnement {
    private String typeAbonnement;
    private LocalDateTime debut;
    private LocalDateTime fin;

    public Abonnement() {}

    public Abonnement(String typeAbonnement, LocalDateTime debut, LocalDateTime fin) {
        this.typeAbonnement = typeAbonnement;
        this.debut = debut;
        this.fin = fin;
    }

    public AbonnementJSON toJSON() {
        return new AbonnementJSON(this.typeAbonnement, this.debut, this.fin);
    }

    public String getType() {
        return this.typeAbonnement;
    }

    public LocalDateTime getDebut() {
        return this.debut;
    }

    public LocalDateTime getFin() {
        return this.fin;
    }

    public void setType(String typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public boolean isValid() {
        if (this.fin == null) {
            return false;
        }
        return this.fin.isAfter(LocalDateTime.now());
    }
}