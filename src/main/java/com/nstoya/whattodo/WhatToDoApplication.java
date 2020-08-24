package com.nstoya.whattodo;

import com.nstoya.whattodo.auth.WhatToDoAuthorizer;
import com.nstoya.whattodo.auth.WhatToDoOAuthAuthenticator;
import com.nstoya.whattodo.core.User;
import com.nstoya.whattodo.core.entity.Task;
import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.db.TaskDAO;
import com.nstoya.whattodo.db.ToDoDAO;
import com.nstoya.whattodo.health.ResourcesHealthCheck;
import com.nstoya.whattodo.resources.ToDosResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.validation.Valid;
import javax.validation.Validator;


public class WhatToDoApplication extends Application<WhatToDoConfiguration> {
    public static void main(final String[] args) throws Exception {
        new WhatToDoApplication().run(args);
    }

    //db
    private final HibernateBundle<WhatToDoConfiguration> hibernate = new HibernateBundle<WhatToDoConfiguration>(ToDo.class, Task.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(WhatToDoConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "What To Do";
    }

    @Override
    public void initialize(final Bootstrap<WhatToDoConfiguration> bootstrap) {

        bootstrap.addBundle(hibernate);

        bootstrap.addBundle(new MigrationsBundle<WhatToDoConfiguration>(){
            @Override
            public DataSourceFactory getDataSourceFactory(WhatToDoConfiguration configuration){
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final WhatToDoConfiguration configuration,
                    final Environment environment) {


        //register Auth
        AuthFilter<String, User> oauthFilter = new OAuthCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new WhatToDoOAuthAuthenticator(configuration.getAuthToken()))
                .setAuthorizer(new WhatToDoAuthorizer())
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(oauthFilter));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        Validator validator = environment.getValidator();

        //register resources
        final ToDosResource resource = new ToDosResource(new ToDoDAO(hibernate.getSessionFactory()),
                new TaskDAO(hibernate.getSessionFactory()), environment.getValidator());
        environment.jersey().register(resource);

        //register healthCheck
        final ResourcesHealthCheck resourcesHealthCheck =
                new ResourcesHealthCheck(configuration.getHealthCheckPath(), configuration.getHealthCheckToken());
        environment.healthChecks().register("resources", resourcesHealthCheck);
    }

}
