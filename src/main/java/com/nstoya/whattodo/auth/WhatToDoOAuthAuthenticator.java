package com.nstoya.whattodo.auth;

import com.nstoya.whattodo.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class WhatToDoOAuthAuthenticator implements Authenticator<String, User> {

    private String acceptedToken;

    public WhatToDoOAuthAuthenticator(){}

    public WhatToDoOAuthAuthenticator(String acceptedToken){
        this.acceptedToken = acceptedToken;
    }

    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        //TODO user table, token generation, token expires in, client_id, client_secret
        if(token.equals(acceptedToken)){
            return Optional.of(new User("nadezhda","password", "admin"));
        }
        return Optional.empty();
     }
}
