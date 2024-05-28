package com.gabriel.projetoestacio.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gabriel.projetoestacio.entities.UsuarioLogado;

@Repository
public interface UsuarioLogadoRepository extends JpaRepository<UsuarioLogado, Long> {
    List<UsuarioLogado> findByMomentoCriacaoBefore(LocalDateTime time);
    boolean existsByToken(String token);
    void deleteByToken(String token);
    Optional<UsuarioLogado> findByToken(String token);
    Optional<UsuarioLogado> findByIdUsuario(Long idUsuario);
}