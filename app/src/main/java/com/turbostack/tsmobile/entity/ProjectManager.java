package com.example.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {
    private List<Project> projects;

    public ProjectManager() {
        this.projects = new ArrayList<>();
    }

    public boolean checkIfExist(String name) {
        for (Project project : projects) {
            if (project.getNom() != null && project.getNom().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<Project> getAll() {
        return this.projects;
    }

    public void create(Project project) {
        Project tmp = new Project();
        tmp.setNom(project.getNom());
        tmp.setType(project.getType());
        tmp.setCreatedAt(LocalDateTime.now());
        tmp.setUpdateAt(LocalDateTime.now());
        this.projects.add(tmp);
    }

    public void update(Project project) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getNom().equals(project.getNom())) {
                projects.set(i, project);
                break;
            }
        }
    }

    public void delete(Project project) {
        projects.removeIf(val -> val.getNom().equals(project.getNom()));
    }

    public Project getByName(String name) {
        for (Project val : projects) {
            if (val.getNom().equals(name)) {
                return val;
            }
        }
        return null;
    }
}