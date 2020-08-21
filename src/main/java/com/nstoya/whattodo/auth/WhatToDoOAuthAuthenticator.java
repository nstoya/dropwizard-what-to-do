package com.nstoya.whattodo.auth;

import com.nstoya.whattodo.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class WhatToDoOAuthAuthenticator implements Authenticator<String, User> {


    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        //TODO user table, token generation
        if(token.equals("test-token")){
            return Optional.of(new User("nadezhda","password", "admin"));
        }
        return Optional.empty();
     }
}
