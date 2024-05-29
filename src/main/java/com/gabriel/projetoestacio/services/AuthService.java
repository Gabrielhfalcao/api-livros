package com.gabriel.projetoestacio.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gabriel.projetoestacio.DTO.PostRequestDTO;
import com.gabriel.projetoestacio.entities.Categoria;
import com.gabriel.projetoestacio.entities.Livro;
import com.gabriel.projetoestacio.entities.PasswordResetToken;
import com.gabriel.projetoestacio.entities.Post;
import com.gabriel.projetoestacio.entities.Usuario;
import com.gabriel.projetoestacio.entities.UsuarioLogado;
import com.gabriel.projetoestacio.repositories.CategoriaRepository;
import com.gabriel.projetoestacio.repositories.LivroRepository;
import com.gabriel.projetoestacio.repositories.PasswordResetTokenRepository;
import com.gabriel.projetoestacio.repositories.PostRepository;
import com.gabriel.projetoestacio.repositories.UsuarioLogadoRepository;
import com.gabriel.projetoestacio.repositories.UsuarioRepository;

@Service
public class AuthService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioLogadoRepository usuarioLogadoRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private LivroRepository livroRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ImagemPerfilService imagemPerfilService;

	public String authenticate(String emailOrUsuario, String senha) {
		Optional<Usuario> userOpt = usuarioRepository.findByEmailOrUsuario(emailOrUsuario, emailOrUsuario);
		if (userOpt.isPresent()) {
			Usuario user = userOpt.get();
			if (!user.isEmailValidated()) {
				return "Por favor, valide seu e-mail antes de fazer login.";
			}
			if (user.getSenha().equals(senha)) {
				Optional<UsuarioLogado> existingLogado = usuarioLogadoRepository.findByIdUsuario(user.getId());
				if (existingLogado.isPresent()) {
					return "Usuário já está logado.";
				}

				String token = UUID.randomUUID().toString();
				UsuarioLogado usuarioLogado = new UsuarioLogado();
				usuarioLogado.setToken(token);
				usuarioLogado.setIdUsuario(user.getId());
				usuarioLogado.setMomentoCriacao(LocalDateTime.now());
				usuarioLogadoRepository.save(usuarioLogado);
				return token;
			} else {
				return "Usuário ou senha incorretos.";
			}
		} else {
			return "Usuário ou senha incorretos.";
		}
	}

	public String logout(String token) {
		Optional<UsuarioLogado> usuarioLogadoOpt = usuarioLogadoRepository.findByToken(token);
		if (usuarioLogadoOpt.isPresent()) {
			usuarioLogadoRepository.delete(usuarioLogadoOpt.get());
			return "Logout realizado com sucesso.";
		} else {
			return "Token inválido.";
		}
	}

	public Optional<Long> verificarUsuarioLogado(String token) {
		Optional<UsuarioLogado> usuarioLogadoOpt = usuarioLogadoRepository.findByToken(token);
		if (usuarioLogadoOpt.isPresent()) {
			UsuarioLogado usuarioLogado = usuarioLogadoOpt.get();
			if (usuarioLogado.getMomentoCriacao().isAfter(LocalDateTime.now().minusMinutes(20))) {
				return Optional.of(usuarioLogado.getIdUsuario());
			} else {
				usuarioLogadoRepository.delete(usuarioLogado);
			}
		}
		return Optional.empty();
	}

	public void atualizarAtividadeUsuario(String token) {
		Optional<UsuarioLogado> usuarioLogadoOpt = usuarioLogadoRepository.findByToken(token);
		if (usuarioLogadoOpt.isPresent()) {
			UsuarioLogado usuarioLogado = usuarioLogadoOpt.get();
			usuarioLogado.setMomentoCriacao(LocalDateTime.now());
			usuarioLogadoRepository.save(usuarioLogado);
		}
	}

	public String addPostForLoggedUser(String token, PostRequestDTO postRequestDTO, MultipartFile fotoLivro1,
			MultipartFile fotoLivro2) throws IOException {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (userIdOpt.isPresent()) {
			Long userId = userIdOpt.get();
			Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
			if (usuarioOpt.isPresent()) {
				Usuario usuario = usuarioOpt.get();

				Optional<Categoria> categoriaOpt = categoriaRepository
						.findByCategoria(postRequestDTO.getCategoriaLivro());
				if (!categoriaOpt.isPresent()) {
					return "Categoria não encontrada.";
				}

				Categoria categoria = categoriaOpt.get();
				Livro livro = new Livro();
				livro.setTitulo(postRequestDTO.getTituloLivro());
				livro.setAutor(postRequestDTO.getAutorLivro());
				livro.setIdioma(postRequestDTO.getIdiomaLivro());
				livro.setCategoria(categoria);

				livroRepository.save(livro);

				Post post = new Post();
				post.setUsuario(usuario);
				post.setLivro(livro);
				post.setDescricao(postRequestDTO.getDescricao());

				String fotoLivro1Filename = imagemPerfilService.salvarImagemLivro(fotoLivro1, null);
				String fotoLivro2Filename = imagemPerfilService.salvarImagemLivro(fotoLivro2, null);
				post.setFotoLivro1(fotoLivro1Filename);
				post.setFotoLivro2(fotoLivro2Filename);

				postRepository.save(post);

				usuario.addPost(post);
				usuarioRepository.save(usuario);

				return "Post adicionado com sucesso.";
			} else {
				return "Usuário não encontrado.";
			}
		} else {
			return "Usuário não está logado.";
		}
	}

	public String apagarFotoLivro1(Post post, String token) throws IOException {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (userIdOpt.isPresent()) {
			imagemPerfilService.apagarImagem(post.getFotoLivro1());
			post.setFotoLivro1(null);
			postRepository.save(post);
			return "Foto 1 excluída com sucesso.";
		} else {
			return "Usuário não está logado.";
		}
	}

	public String apagarFotoLivro2(Post post, String token) throws IOException {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (userIdOpt.isPresent()) {
			imagemPerfilService.apagarImagem(post.getFotoLivro2());
			post.setFotoLivro2(null);
			postRepository.save(post);
			return "Foto 2 excluída com sucesso.";
		} else {
			return "Usuário não está logado.";
		}
	}

	public String deletePostForLoggedUser(String token, Long postId) {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (userIdOpt.isPresent()) {
			Long userId = userIdOpt.get();
			Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
			if (usuarioOpt.isPresent()) {
				Usuario usuario = usuarioOpt.get();
				Optional<Post> postOpt = postRepository.findById(postId);
				if (postOpt.isPresent()) {
					Post post = postOpt.get();
					if (post.getUsuario().getId().equals(userId)) {
						usuario.getPublicacoes().remove(post);
						usuarioRepository.save(usuario);
						postRepository.delete(post);
						return "Post excluído com sucesso.";
					} else {
						return "Você não tem permissão para excluir este post.";
					}
				} else {
					return "Post não encontrado.";
				}
			} else {
				return "Usuário não encontrado.";
			}
		} else {
			return "Usuário não está logado.";
		}
	}

	public String addPostToFavorites(String token, Long postId) {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (!userIdOpt.isPresent()) {
			return "Usuário não está logado.";
		}

		Long userId = userIdOpt.get();
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
		if (!usuarioOpt.isPresent()) {
			return "Usuário não encontrado.";
		}

		Optional<Post> postOpt = postRepository.findById(postId);
		if (!postOpt.isPresent()) {
			return "Post não encontrado.";
		}

		Usuario usuario = usuarioOpt.get();
		Post post = postOpt.get();

		if (usuario.getFavoritos().contains(post)) {
			return "Post já está nos favoritos.";
		}

		usuario.addFavoritos(post);
		usuarioRepository.save(usuario);
		return "Post adicionado aos favoritos com sucesso.";
	}

	public String removePostFromFavorites(String token, Long postId) {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (!userIdOpt.isPresent()) {
			return "Usuário não está logado.";
		}

		Long userId = userIdOpt.get();
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
		if (!usuarioOpt.isPresent()) {
			return "Usuário não encontrado.";
		}

		Optional<Post> postOpt = postRepository.findById(postId);
		if (!postOpt.isPresent()) {
			return "Post não encontrado.";
		}

		Usuario usuario = usuarioOpt.get();
		Post post = postOpt.get();

		if (!usuario.getFavoritos().contains(post)) {
			return "Post não está nos favoritos.";
		}

		usuario.getFavoritos().remove(post);
		usuarioRepository.save(usuario);
		return "Post removido dos favoritos com sucesso.";
	}

	public String alterarSenha(String token, String senhaAntiga, String senhaNova) {
		Optional<Long> userIdOpt = verificarUsuarioLogado(token);
		if (userIdOpt.isPresent()) {
			Long userId = userIdOpt.get();
			Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
			if (usuarioOpt.isPresent()) {
				Usuario usuario = usuarioOpt.get();
				if (usuario.getSenha().equals(senhaAntiga)) {
					usuario.setSenha(senhaNova);
					usuarioRepository.save(usuario);
					return "Senha alterada com sucesso.";
				} else {
					return "Senha antiga incorreta.";
				}
			} else {
				return "Usuário não encontrado.";
			}
		} else {
			return "Usuário não está logado.";
		}
	}

	public String requestPasswordReset(String email) {
		Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
		if (userOpt.isPresent()) {
			Usuario user = userOpt.get();
			String token = UUID.randomUUID().toString();
			PasswordResetToken passwordResetToken = new PasswordResetToken(user.getId(), token,
					LocalDateTime.now().plusHours(1));
			passwordResetTokenRepository.save(passwordResetToken);

			String resetLink = token;
			emailService.sendSimpleMessage(email, "Mudança de senha", "Token para mudança de senha: " + resetLink);
			return "Token para mudança de senha enviado para seu email.";
		} else {
			return "Email não encontrado";
		}
	}

	public String resetPassword(String token, String newPassword) {
		Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
		if (tokenOpt.isPresent()) {
			PasswordResetToken passwordResetToken = tokenOpt.get();
			if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
				return "O Token expirou";
			}

			Optional<Usuario> userOpt = usuarioRepository.findById(passwordResetToken.getUserId());
			if (userOpt.isPresent()) {
				Usuario user = userOpt.get();
				user.setSenha(newPassword);
				usuarioRepository.save(user);
				passwordResetTokenRepository.deleteByToken(token);
				return "Senha alterada com sucesso.";
			} else {
				return "Usuário não encontrado.";
			}
		} else {
			return "Token inválido.";
		}
	}
	
	public String editarPost(String token, Long id, PostRequestDTO postRequestDTO) {
	    Optional<Long> userIdOpt = verificarUsuarioLogado(token);
	    if (userIdOpt.isPresent()) {
	        Long userId = userIdOpt.get();
	        Optional<Post> postOpt = postRepository.findById(id);
	        if (postOpt.isPresent()) {
	            Post post = postOpt.get();
	            if (post.getUsuario().getId().equals(userId)) {
	                post.setDescricao(postRequestDTO.getDescricao());
	                post.getLivro().setTitulo(postRequestDTO.getTituloLivro());
	                post.getLivro().setAutor(postRequestDTO.getAutorLivro());
	                post.getLivro().setIdioma(postRequestDTO.getIdiomaLivro());
	                post.getLivro().setCategoria(categoriaRepository.findByCategoria(postRequestDTO.getCategoriaLivro()).orElse(null));

	                postRepository.save(post);

	                return "Post editado com sucesso.";
	            } else {
	                return "Você não tem permissão para editar este post.";
	            }
	        } else {
	            return "Post não encontrado.";
	        }
	    } else {
	        return "Usuário não está logado.";
	    }
	}

}