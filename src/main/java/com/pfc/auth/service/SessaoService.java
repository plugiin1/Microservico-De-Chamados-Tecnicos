package com.pfc.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public void encerrarSessoes(String username) {
        sessionRepository.findByPrincipalName(username)
                .keySet()
                .forEach(sessionRepository::deleteById);
    }
}
