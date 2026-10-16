package com.example.app.entity;

import java.time.LocalDateTime;

public class AbonnementJSON {
    public String type_abonnement;
    public LocalDateTime debut;
    public LocalDateTime fin;

    public AbonnementJSON() {}

    public AbonnementJSON(String typeAbonnement, LocalDateTime debut, LocalDateTime fin) {
        this.type_abonnement = typeAbonnement;
        this.debut = debut;
        this.fin = fin;
    }

    public Abonnement toModel() {
        return new Abonnement(this.type_abonnement, this.debut, this.fin);
    }
}