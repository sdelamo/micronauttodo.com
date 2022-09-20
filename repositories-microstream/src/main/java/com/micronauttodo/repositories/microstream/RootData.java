package com.micronauttodo.repositories.microstream;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class RootData {
    Todos todos = new Todos();
    Users users = new Users();

    public Todos getTodos() {
        return todos;
    }

    public Users getUsers() {
        return users;
    }
}
