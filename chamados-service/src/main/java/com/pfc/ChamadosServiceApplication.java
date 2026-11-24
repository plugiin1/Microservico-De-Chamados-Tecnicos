package com.pfc;

import com.pfc.chamados.config.DiagnosticoConexao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChamadosServiceApplication {

    public static void main(String[] args) {
        DiagnosticoConexao.executar();
        SpringApplication.run(ChamadosServiceApplication.class, args);
    }
}
