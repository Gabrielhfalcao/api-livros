package com.gabriel.projetoestacio.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gabriel.projetoestacio.entities.Post;
import com.gabriel.projetoestacio.entities.Usuario;

import com.gabriel.projetoestacio.services.UsuarioService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/usuarios")
public class UsuarioResource {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<List<Usuario>> findAll() {
		List<Usuario> usuarios = usuarioService.findAll();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Usuario> findById(@PathVariable Long id) {
		Usuario usuario = usuarioService.findById(id);
		return ResponseEntity.ok(usuario);
	}

	@GetMapping("/{userId}/publicacoes")
	public ResponseEntity<List<Post>> getPublicacoesByUserId(@PathVariable Long userId) {
		try {
			List<Post> publicacoes = usuarioService.getPublicacoesByUserId(userId);
			return ResponseEntity.ok(publicacoes);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/{userId}/favoritos")
	public ResponseEntity<List<Post>> getFavoritosByUserId(@PathVariable Long userId) {
		try {
			List<Post> favoritos = usuarioService.getFavoritosByUserId(userId);
			return ResponseEntity.ok(favoritos);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/cadastrarUsuario")
	public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
		try {
			Usuario newUsuario = usuarioService.cadastrarUsuario(usuario);
			return ResponseEntity.ok(newUsuario);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/validarEmail")
	public ResponseEntity<String> validarEmail(@RequestParam String token) {
		try {
			usuarioService.validarEmail(token);
			return ResponseEntity.ok("E-mail validado com sucesso.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Token de validação inválido ou expirado.");
		}
	}
}
