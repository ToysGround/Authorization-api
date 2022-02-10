package com.security.tokensecurity.domain.repository;

import com.security.tokensecurity.domain.entity.TokenTb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenTbRepository extends JpaRepository<TokenTb, Long> {
    @Query("select t from TokenTb t where t.hashKey = :target")
    TokenTb findByHashKey(@Param("target") String hashKey);

}