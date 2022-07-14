package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import com.micronauttodo.persistence.User;
import com.micronauttodo.persistence.UserRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Requires(beans = { DynamoConfiguration.class, DynamoDbClient.class })
@Singleton
public class UserRepositoryDynamo extends DynamoRepository<OAuthUser> implements UserRepository {

    public static final String SUB = "sub";
    public static final String EMAIL = "email";
    public static final String ISS = "iss";

    public UserRepositoryDynamo(DynamoDbClient dynamoDbClient,
                                DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbClient, dynamoConfiguration);
    }

    @Override
    public void save(@NonNull @NotNull @Valid OAuthUser user) {
        save(user, this::item);
    }

    @Override
    public void delete(@NonNull @NotNull @Valid OAuthUser user) {
        super.delete(pk(user), sk(user));
    }

    @Override
    @NonNull
    protected Map<String, AttributeValue> item(@NonNull OAuthUser user) {
        Map<String, AttributeValue> result = super.item(user);
        result.putAll(itemForOauthUser(user));
        return result;
    }

    @Override
    @NonNull
    protected AttributeValue pk(@NonNull OAuthUser entity) {
        return s(pkStr(entity));
    }

    public static String pkStr(@NonNull OAuthUser entity) {
        return "USER" + HASH + encodeIssAndSub(entity);
    }

    public static String encodeIssAndSub(@NonNull OAuthUser oAuthUser) {
        return new String(
                Base64.getEncoder()
                        .encode((oAuthUser.getIss() + HASH + oAuthUser.getSub())
                                .getBytes(StandardCharsets.UTF_8)));
    }

    public static Map<String, AttributeValue> itemForOauthUser(@NonNull OAuthUser user) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put(SUB, AttributeValue.builder().s(user.getSub()).build());
        result.put(EMAIL, AttributeValue.builder().s(user.getEmail()).build());
        result.put(ISS, AttributeValue.builder().s(user.getIss()).build());
        return result;
    }
}
