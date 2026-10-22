package com.pfc.chamados.repository;

import com.pfc.chamados.entity.Chamado;
import com.pfc.chamados.entity.StatusChamado;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChamadoRepository extends MongoRepository<Chamado, String> {

    List<Chamado> findBySolicitanteIdOrderByDataAberturaDesc(String solicitanteId);

    List<Chamado> findBySolicitanteIdAndStatusOrderByDataAberturaDesc(String solicitanteId, StatusChamado status);
}
