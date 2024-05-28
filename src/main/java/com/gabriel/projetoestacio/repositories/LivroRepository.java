package com.gabriel.projetoestacio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gabriel.projetoestacio.entities.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{

}
