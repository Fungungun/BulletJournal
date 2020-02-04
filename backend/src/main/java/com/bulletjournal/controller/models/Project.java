package com.bulletjournal.controller.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Project {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
    
    @NotBlank
    @Size(min = 1, max = 100)
    private String owner;

    public Project() {
    }

    public Project(Long id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}