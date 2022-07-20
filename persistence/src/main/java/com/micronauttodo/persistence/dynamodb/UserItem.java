package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.OAuthUser;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.micronauttodo.persistence.dynamodb.DynamoRepository.HASH;

@Introspected
public class UserItem extends AbstractItem<OAuthUser> {

    public UserItem(OAuthUser user) {
        super(pkStr(user), pkStr(user), user);
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
}
