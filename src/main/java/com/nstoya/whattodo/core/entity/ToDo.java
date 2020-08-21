package com.nstoya.whattodo.core.entity;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WHATTODO")
@Where(clause = "PARENT is null")
public class ToDo extends WhatToDoA{

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Size(max = 25, message = "Only 25 tasks per Todo are possible.")
    private List<Task> tasks;

    public ToDo(){}

    public ToDo(String name, String description){
        super(name, description);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        if(this.tasks != null){
            this.tasks.removeAll(this.tasks);
            this.tasks.addAll(tasks);
        }else {
            this.tasks = tasks;
        }

    }
}
