package com.micronauttodo;

import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.annotation.Introspected;

import java.util.Collection;

@Introspected
public class Project {
    private final String name;

    private final String domainName;

    private final Collection<Module> modules;

    public Project(String name,
                   String domainName,
                   Collection<Module> modules) {
        this.name = name;
        this.domainName = domainName;
        this.modules = modules;
    }

    public Collection<Module> getModules() {
        return modules;
    }

    public String getName() {
        return name;
    }

    public String getDomainName() {
        return domainName;
    }

    public Module findModuleByName(String name) throws ConfigurationException {
        return getModules()
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException("Module function-cognito-post-confirmation does not exists"));
    }
}
