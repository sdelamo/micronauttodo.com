package com.micronauttodo.repositories.data;

import com.micronauttodo.repositories.data.entities.OAuthUserEntity;
import com.micronauttodo.repositories.data.entities.OAuthUserId;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserJdbcRepository extends CrudRepository<OAuthUserEntity, OAuthUserId> {
}
