package com.nstoya.whattodo.core;

import java.security.Principal;

public class User implements Principal {
    private final String name;
    private final String role;
    private final String password;


    public User(String name, String password, String role){
        this.name = name;
        this.role = role;
        this.password = password;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getRole(){
        return role;
    }

    public String getPassword(){
        return password;
    }
}
