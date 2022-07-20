package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.UserRepository;
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
