package com.nstoya.whattodo.core.entity;

import javax.persistence.*;


@Entity
@Table(name = "WHATTODO")
//@NamedQueries(
//        {
//                @NamedQuery(
//                        name = "com.nstoya.whattodo.core.entity.ToDo.findAllTasks",
//                        query = "SELECT e FROM Task e where PARENT "
//                )
//        })
public class Task extends WhatToDoA{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT")
    private ToDo parent;

    public Task(String name, String description) {
        super(name, description);
    }

    public ToDo getParent() {
        return parent;
    }

    public void setParent(ToDo parent) {
        this.parent = parent;
    }
}
