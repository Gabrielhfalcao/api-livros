package com.gabriel.projetoestacio.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gabriel.projetoestacio.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByUsuario(String usuario);
    Optional<Usuario> findByEmailOrUsuario(String email, String usuario);
	Optional<Usuario> findByValidationToken(String token);
}
