package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.UserRepository;
import com.micronauttodo.repositories.dynamodb.constants.DynamoConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Requires(beans = { DynamoConfiguration.class, DynamoDbClient.class })
@Singleton
public class UserRepositoryDynamo extends DynamoRepository implements UserRepository {

    public static final String SUB = "sub";
    public static final String EMAIL = "email";
    public static final String ISS = "iss";

    public UserRepositoryDynamo(DynamoDbClient dynamoDbClient,
                                DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbClient, dynamoConfiguration);
    }

    @Override
    public void save(@NonNull @NotNull @Valid OAuthUser user) {
        UserItem userItem = new UserItem(user);
        save(userItem.toItem());
    }

    @Override
    public void delete(@NonNull @NotNull @Valid OAuthUser user) {
        super.delete(new UserItem(user).toKey());
    }
}
