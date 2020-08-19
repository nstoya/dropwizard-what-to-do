package com.nstoya.whattodo.core.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WHATTODO")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.nstoya.whattodo.core.entity.ToDo.findAllTodos",
                        query = "SELECT e FROM ToDo e"
                )
        })
public class ToDo extends WhatToDoA{

    @OneToMany(mappedBy = "parent")
    private List<Task> tasks = new ArrayList<>();

    public ToDo(String name, String description){
        super(name, description);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
