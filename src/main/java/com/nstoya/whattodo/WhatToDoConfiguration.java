package com.nstoya.whattodo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class WhatToDoConfiguration extends Configuration {

    @NotEmpty
    private String healthCheckPath;

    @NotEmpty
    private String healthCheckToken;
    @Valid
    @NotNull
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();


    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @JsonProperty
    public String getHealthCheckPath(){
        return healthCheckPath;
    }

    @JsonProperty
    public String getHealthCheckToken(){
        return healthCheckToken;
    }
}
