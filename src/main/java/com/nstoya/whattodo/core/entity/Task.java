package com.nstoya.whattodo.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Entity
@Table(name = "WHATTODO")
@Where(clause = "PARENT is not null")

public class Task extends WhatToDoA{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT")
    @JsonIgnore
    private ToDo parent;

    public Task(){}

    public Task(String name, String description) {
        super(name, description);
    }

    @JsonIgnore
    public ToDo getParent() {
        return parent;
    }

    @JsonIgnore
    public void setParent(ToDo parent) {
        this.parent = parent;
    }
}
