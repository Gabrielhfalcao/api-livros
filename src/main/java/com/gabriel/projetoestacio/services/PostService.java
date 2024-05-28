package com.gabriel.projetoestacio.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabriel.projetoestacio.entities.Post;
import com.gabriel.projetoestacio.repositories.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Post create(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> update(Long id, Post postDetails) {
        return postRepository.findById(id).map(post -> {
            post.setUsuario(postDetails.getUsuario());
            post.setLivro(postDetails.getLivro());
            post.setDescricao(postDetails.getDescricao());
            return postRepository.save(post);
        });
    }

    public boolean delete(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}