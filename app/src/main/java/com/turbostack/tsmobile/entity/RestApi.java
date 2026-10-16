package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;

public class RestApi {
    private List<Endpoint> endpoints;
    private List<String> role;
    private BDD bdd;

    public RestApi() {
        this.endpoints = new ArrayList<>();
        this.role = new ArrayList<>();
        this.bdd = new BDD();
    }

    public RestApiJSON toJSON() {
        List<EndpointJSON> endpointJSONs = new ArrayList<>();
        for (Endpoint val : this.endpoints) {
            endpointJSONs.add(val.toJSON());
        }
        return new RestApiJSON(endpointJSONs, this.role, this.bdd.toJSON());
    }

    public List<Endpoint> getEndpoints() { return endpoints; }
    public RestApi setEndpoints(List<Endpoint> endpoints) { this.endpoints = endpoints; return this; }

    public List<String> getRole() { return role; }
    public RestApi setRole(List<String> role) { this.role = role; return this; }

    public BDD getBdd() { return bdd; }
    public RestApi setBdd(BDD bdd) { this.bdd = bdd; return this; }
}