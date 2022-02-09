package com.security.tokensecurity.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TOKEN_TB", indexes = {
        @Index(name = "ix_hash", columnList = "HASH_KEY")
})
public class TokenTb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_SQ", nullable = false)
    private long id;

    @Column(name = "HASH_KEY", nullable = false, length = 32)
    private String hashKey;

    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String refreshToken;

    @Column(name = "GOURP_NO", nullable = false, length = 2)
    private String gourpNo;

    @Column(name = "REG_DT")
    private Instant regDt;

}