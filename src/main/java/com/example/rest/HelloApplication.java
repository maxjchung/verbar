package com.example.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class HelloApplication extends Application
{
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public HelloApplication() throws Exception {
        // ADD YOUR RESTFUL RESOURCES HERE
        this.singletons.add(new Hello());
        this.singletons.add(new GuidedLegal());
    }

    public Set<Class<?>> getClasses()
    {
        return this.empty;
    }

    public Set<Object> getSingletons()
    {
        return this.singletons;
    }
}

