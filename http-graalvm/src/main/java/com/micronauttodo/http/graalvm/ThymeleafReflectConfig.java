package com.micronauttodo.http.graalvm;

import com.micronauttodo.models.Todo;
import com.micronauttodo.views.Model;
import com.micronauttodo.views.TodoModel;
import com.micronauttodo.views.UserModel;
import io.micronaut.core.annotation.TypeHint;

import java.util.Optional;

@TypeHint(value = {
        Model.class,
        UserModel.class,
        TodoModel.class,
        Optional.class,
        Todo.class
}, accessType = TypeHint.AccessType.ALL_PUBLIC_METHODS)
public class ThymeleafReflectConfig {
}
