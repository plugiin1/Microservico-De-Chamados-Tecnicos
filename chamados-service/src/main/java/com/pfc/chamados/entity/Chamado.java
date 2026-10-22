package com.pfc.chamados.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chamados")
@Getter
@Setter
@NoArgsConstructor
public class Chamado {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    private StatusChamado status;

    @Indexed
    private String solicitanteId;

    private String solicitanteUsername;

    private Instant dataAbertura;

    private String solucao;

    private String tecnicoId;

    private String tecnicoUsername;

    private Instant dataResolucao;

    @Version
    private Long versao;
}
