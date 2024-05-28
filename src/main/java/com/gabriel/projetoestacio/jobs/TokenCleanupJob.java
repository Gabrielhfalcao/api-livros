package com.gabriel.projetoestacio.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gabriel.projetoestacio.entities.UsuarioLogado;
import com.gabriel.projetoestacio.repositories.UsuarioLogadoRepository;

@Component
public class TokenCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupJob.class);

    @Autowired
    private UsuarioLogadoRepository usuarioLogadoRepository;

    @Scheduled(fixedRate = 60000)
    public void cleanUpExpiredTokens() {
        logger.info("Job de limpeza de token iniciado");
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(20);
        List<UsuarioLogado> expiredTokens = usuarioLogadoRepository.findByMomentoCriacaoBefore(tenMinutesAgo);
        if (expiredTokens.isEmpty()) {
            logger.info("Sem tokens expirados");
        } else {
            usuarioLogadoRepository.deleteAll(expiredTokens);
            logger.info("Deletado, {} token(s) expirados(s)", expiredTokens.size());
        }
        logger.info("Job de limpeza de token finalizado");
    }
}