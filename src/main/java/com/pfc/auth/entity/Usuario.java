package com.pfc.auth.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String senha;

    private Role role = Role.CLIENTE;

    private boolean ativo = true;

    private Instant criadoEm = Instant.now();
}
