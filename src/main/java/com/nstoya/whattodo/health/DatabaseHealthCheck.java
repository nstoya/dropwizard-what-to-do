package com.nstoya.whattodo.health;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.DataSourceFactory;

public class DatabaseHealthCheck extends HealthCheck {

    private final DataSourceFactory dataSourceFactory;

    public DatabaseHealthCheck(DataSourceFactory dataSourceFactory){
        this.dataSourceFactory = dataSourceFactory;

    }
    @Override
    protected Result check() throws Exception {

        System.err.println(">>>>>>>>>>>> " + dataSourceFactory.getValidationQuery());
        //TODO check DB Connectivity
        return Result.healthy();
    }
}
