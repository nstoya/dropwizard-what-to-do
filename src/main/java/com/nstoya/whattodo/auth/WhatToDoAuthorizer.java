package com.nstoya.whattodo.auth;

import com.nstoya.whattodo.core.User;
import io.dropwizard.auth.Authorizer;

import javax.annotation.Nullable;
import javax.ws.rs.container.ContainerRequestContext;

public class WhatToDoAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(User user, String role) {
        return user.getRole() != null && user.getRole().equals(role);

    }

    @Override
    public boolean authorize(User user, String role, @Nullable ContainerRequestContext requestContext) {
        return user.getRole() != null && user.getRole().equals(role);
    }
}
