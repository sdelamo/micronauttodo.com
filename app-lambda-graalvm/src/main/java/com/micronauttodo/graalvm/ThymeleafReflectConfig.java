package com.micronauttodo.graalvm;

import com.micronauttodo.persistence.Todo;
import com.micronauttodo.views.Model;
import com.micronauttodo.views.TodoModel;
import io.micronaut.core.annotation.TypeHint;

import java.util.Optional;

@TypeHint(value = {
        Model.class,
        Optional.class,
        TodoModel.class,
        Todo.class
}, accessType = TypeHint.AccessType.ALL_PUBLIC_METHODS)
public class ThymeleafReflectConfig {
}
