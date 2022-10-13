package com.micronauttodo.repositories.microstream;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.TodoRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.microstream.annotations.StoreParams;
import io.micronaut.microstream.annotations.StoreReturn;
import io.micronaut.microstream.annotations.StoreRoot;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.micronaut.microstream.RootProvider;

@Singleton
public class TodoRepositoryImpl implements TodoRepository {
    private final RootProvider<RootData> rootProvider;

    public TodoRepositoryImpl(RootProvider<RootData> rootProvider) {
        this.rootProvider = rootProvider;
    }

    @Override
    public void delete(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid OAuthUser user) {
        if (rootProvider.root().getTodos().containsKey(user.getSub())) {
            deleteTodos(rootProvider.root().getTodos(), id, user);
        }
    }

    @Override
    public void save(@NonNull @NotNull @Valid Todo todo, @NonNull @NotNull @Valid OAuthUser user) {
        if (rootProvider.root().getTodos().containsKey(user.getSub())) {
            append(rootProvider.root().getTodos(), todo, user);
        } else {
            save(rootProvider.root().getTodos(), todo, user);
        }
    }

    @Override
    @NonNull
    public List<Todo> findAll(@NotNull @Valid OAuthUser oAuthUser) {
        return todos().containsKey(oAuthUser.getSub()) ?
                new ArrayList<>(todos().get(oAuthUser.getSub()).values()) :
                Collections.emptyList();
    }

    @Override
    public Optional<Todo> findById(@NotBlank String id, @NotNull @Valid OAuthUser user) {
        return todos().containsKey(user.getSub()) ?
                Optional.ofNullable(todos().get(user.getSub()).get(id)):
                Optional.empty();
    }

    /**
     * The rule is: "The Object that has been modified has to be stored!".
     * To store a newly created object, store the "owner" of the object.
     */
    @StoreReturn
    Map<String, Todo> deleteTodos(Map<String, Map<String, Todo>> todos,
                                  String id,
                                  OAuthUser user) {
        todos.get(user.getSub()).remove(id);
        return todos.get(user.getSub());
    }

    /**
     * The rule is: "The Object that has been modified has to be stored!".
     * To store a newly created object, store the "owner" of the object.
     */
    @StoreParams("todos")
    Map<String, Todo> save(Map<String, Map<String, Todo>> todos,
                           Todo todo,
                           OAuthUser user) {
        todos.put(user.getSub(), CollectionUtils.mapOf(todo.getId(), todo));
        return todos.get(user.getSub());
    }


    /**
     * The rule is: "The Object that has been modified has to be stored!".
     * To store a newly created object, store the "owner" of the object.
     */
    @StoreReturn
    Map<String, Todo> append(Map<String, Map<String, Todo>> todos,
              Todo todo,
              OAuthUser user) {
        todos.get(user.getSub()).put(todo.getId(), todo);
        return todos.get(user.getSub());
    }

    private Map<String, Map<String, Todo>> todos() {
        return rootProvider.root().getTodos();
    }

    public int countByUser(@NonNull OAuthUser user) {
        return findAll(user).size();
    }
}
