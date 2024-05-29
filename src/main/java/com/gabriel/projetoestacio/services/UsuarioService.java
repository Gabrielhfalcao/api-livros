package com.gabriel.projetoestacio.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabriel.projetoestacio.entities.Post;
import com.gabriel.projetoestacio.entities.Usuario;
import com.gabriel.projetoestacio.exceptions.EmailAlreadyExistsException;
import com.gabriel.projetoestacio.exceptions.UsernameAlreadyExistsException;
import com.gabriel.projetoestacio.repositories.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EmailService emailService;
	
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}
	
	public Usuario findById(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return usuario.get();
	}

	public Usuario cadastrarUsuario(Usuario usuario) {
        Optional<Usuario> existingUsuarioByEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUsuarioByEmail.isPresent()) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }

        Optional<Usuario> existingUsuarioByUsername = usuarioRepository.findByUsuario(usuario.getUsuario());
        if (existingUsuarioByUsername.isPresent()) {
            throw new UsernameAlreadyExistsException("Nome de usuário já cadastrado");
        }

        String validationToken = UUID.randomUUID().toString();
        usuario.setValidationToken(validationToken);
        usuario.setEmailValidated(false);

        Usuario newUsuario = usuarioRepository.save(usuario);

        emailService.sendValidationEmail(usuario.getEmail(), validationToken);

        return newUsuario;
    }
    
    public String addPost(Long userId, Post post) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.addPost(post);
            usuarioRepository.save(usuario);
            return "Post adicionado com sucesso";
        } else {
            return "Usuário não encontrado.";
        }
    }
    
    public List<Post> getPublicacoesByUserId(Long userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (usuarioOpt.isPresent()) {
            return usuarioOpt.get().getPublicacoes();
        } else {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
    }
    
    public List<Post> getFavoritosByUserId(Long userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (usuarioOpt.isPresent()) {
            return usuarioOpt.get().getFavoritos();
        } else {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
    }
    
    public void validarEmail(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByValidationToken(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEmailValidated(true);
            usuario.setValidationToken(null);
            usuarioRepository.save(usuario);
        } else {
            throw new IllegalArgumentException("Token de validação inválido ou expirado.");
        }
    }
}
