package com.nstoya.whattodo.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@MappedSuperclass
public abstract class WhatToDoA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @NotNull (message = "is mandatory but missing.")
    @NotBlank (message = "is not allowed to be an empty string.")//darf weder null noch "" sein
    @Column(name = "NAME", nullable = false)
    private String name;


    @Column(name = "DESCRIPTION")
    private String description;

    public WhatToDoA(){}

    public WhatToDoA(String name, String description){
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
