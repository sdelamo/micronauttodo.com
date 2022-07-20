package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static com.micronauttodo.repositories.dynamodb.DynamoRepository.HASH;

@Introspected
public class UserItem extends AbstractItem<OAuthUser> {

    public UserItem(OAuthUser user) {
        super(key(user), user);
    }

    public static String pkStr(@NonNull OAuthUser user) {
        return Item.getType(OAuthUser.class) + HASH + encodeIssAndSub(user);
    }

    public static String encodeIssAndSub(@NonNull OAuthUser oAuthUser) {
        return new String(
                Base64.getEncoder()
                        .encode((oAuthUser.getIss() + HASH + oAuthUser.getSub())
                                .getBytes(StandardCharsets.UTF_8)));
    }


    private static CompositePrimaryKey key(OAuthUser user) {
        String pk = pkStr(user);
        return new CompositePrimaryKey() {
            @Override
            @NonNull
            public String getPartitionKey() {
                return pk;
            }

            @Override
            @NonNull
            public Optional<String> getSortKey() {
                return Optional.of(pk);
            }
        };
    }
}
