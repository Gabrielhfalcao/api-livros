package com.gabriel.projetoestacio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gabriel.projetoestacio.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

}
