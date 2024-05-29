package com.gabriel.projetoestacio.resources;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gabriel.projetoestacio.DTO.PostRequestDTO;
import com.gabriel.projetoestacio.entities.Post;
import com.gabriel.projetoestacio.entities.Usuario;
import com.gabriel.projetoestacio.repositories.PostRepository;
import com.gabriel.projetoestacio.repositories.UsuarioRepository;
import com.gabriel.projetoestacio.services.AuthService;
import com.gabriel.projetoestacio.services.ImagemPerfilService;


import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ImagemPerfilService imagemPerfilService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PostRepository postRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String emailOrUsuario, @RequestParam String senha) {
        String response = authService.authenticate(emailOrUsuario, senha);
        if (response.startsWith("Usuario ou senha incorreto") || response.startsWith("Usuario já está logado.")) {
            return ResponseEntity.status(401).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token) {
        String response = authService.logout(token);
        if (response.equals("Logout realizado com sucesso.")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
 
    @PostMapping("/addPost")
    public ResponseEntity<String> addPost(@RequestParam("token") String token,
                                          @RequestParam("descricao") String descricao,
                                          @RequestParam("tituloLivro") String tituloLivro,
                                          @RequestParam("autorLivro") String autorLivro,
                                          @RequestParam("idiomaLivro") String idiomaLivro,
                                          @RequestParam("categoriaLivro") String categoriaLivro,
                                          @RequestParam("fotoLivro1") MultipartFile fotoLivro1,
                                          @RequestParam("fotoLivro2") MultipartFile fotoLivro2) {
        PostRequestDTO postRequestDTO = new PostRequestDTO(token, descricao, tituloLivro, autorLivro, idiomaLivro, categoriaLivro);
        try {
            String response = authService.addPostForLoggedUser(token, postRequestDTO, fotoLivro1, fotoLivro2);
            if (response.equals("Post adicionado com sucesso.")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagens.");
        }
    }
    
    @DeleteMapping("/deleteFotoLivro1/{postId}")
    public ResponseEntity<String> deleteFotoLivro1(@PathVariable Long postId, @RequestParam String token) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            try {
                String response = authService.apagarFotoLivro1(post, token);
                authService.atualizarAtividadeUsuario(token);
                return ResponseEntity.ok().body(response);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir a FotoLivro1.");
            }
        } else {
        	authService.atualizarAtividadeUsuario(token);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/deleteFotoLivro2/{postId}")
    public ResponseEntity<String> deleteFotoLivro2(@PathVariable Long postId, @RequestParam String token) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            try {
                String response = authService.apagarFotoLivro2(post, token);
                authService.atualizarAtividadeUsuario(token);
                return ResponseEntity.ok(response);
            } catch (IOException e) {
            	authService.atualizarAtividadeUsuario(token);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir a FotoLivro2.");
            }
        } else {
        	authService.atualizarAtividadeUsuario(token);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletePost/{id}")
    public ResponseEntity<String> deletePost(@RequestParam String token, @PathVariable Long id) {
        authService.atualizarAtividadeUsuario(token);
        String result = authService.deletePostForLoggedUser(token, id);
        if (result.equals("Post excluído com sucesso.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
    }

    @PostMapping("/addFavorito")
    public ResponseEntity<String> addPostToFavorites(@RequestParam String token, @RequestParam Long postId) {
        authService.atualizarAtividadeUsuario(token);
        String result = authService.addPostToFavorites(token, postId);
        if (result.equals("Post adicionado aos favoritos com sucesso.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/removeFavorito/{id}")
    public ResponseEntity<String> removePostFromFavorites(@RequestParam String token, @PathVariable Long id) {
        authService.atualizarAtividadeUsuario(token);
        String result = authService.removePostFromFavorites(token, id);
        if (result.equals("Post removido dos favoritos com sucesso.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
    	String response = authService.requestPasswordReset(email);
    	return ResponseEntity.ok().body(response);
    }
    
    @Transactional
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
    	String response = authService.resetPassword(token, newPassword);
    	return ResponseEntity.ok().body(response);
    }

    @PutMapping("/alterarSenha")
    public ResponseEntity<String> alterarSenha(@RequestParam String token, 
                                               @RequestBody Map<String, String> payload) {
        String senhaAntiga = payload.get("senhaAntiga");
        String senhaNova = payload.get("senhaNova");
        String result = authService.alterarSenha(token, senhaAntiga, senhaNova);
        if (result.equals("Senha alterada com sucesso.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping("/upload-imagem")
    public ResponseEntity<String> uploadImagemPerfil(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        try {
            Optional<Long> usuarioLogadoOpt = authService.verificarUsuarioLogado(token);
            if (!usuarioLogadoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não está logado.");
            }
            authService.atualizarAtividadeUsuario(token);
            String fileName = imagemPerfilService.salvarImagemPerfil(file, token);
            return ResponseEntity.ok().body(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao fazer upload da imagem");
        }
    }

    @GetMapping("/imagem-perfil")
    public ResponseEntity<Resource> carregarImagemPerfil(@RequestParam String token) {
        try {
            Optional<Long> usuarioLogadoIdOpt = authService.verificarUsuarioLogado(token);
            if (!usuarioLogadoIdOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            authService.atualizarAtividadeUsuario(token);
            Long usuarioLogadoId = usuarioLogadoIdOpt.get();
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioLogadoId);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Usuario usuario = usuarioOpt.get();
            if (usuario.getImagemPerfil() == null || usuario.getImagemPerfil().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = imagemPerfilService.carregarImagemPerfil(usuario.getImagemPerfil());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/imagem-livro/{postId}/foto1")
    public ResponseEntity<Resource> carregarImagemLivroFoto1(@PathVariable Long postId) {
        return carregarImagemLivroByFotoNumber(postId, 1);
    }

    @GetMapping("/imagem-livro/{postId}/foto2")
    public ResponseEntity<Resource> carregarImagemLivroFoto2(@PathVariable Long postId, @RequestParam String token) {
    	authService.atualizarAtividadeUsuario(token);
    	return carregarImagemLivroByFotoNumber(postId, 2);
    }

    private ResponseEntity<Resource> carregarImagemLivroByFotoNumber(Long postId, int fotoNumber) {
        try {
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isPresent()) {
                Post post = postOpt.get();
                String fotoFilename;
                if (fotoNumber == 1) {
                    fotoFilename = post.getFotoLivro1();
                } else if (fotoNumber == 2) {
                    fotoFilename = post.getFotoLivro2();
                } else {
                    return ResponseEntity.badRequest().build();
                }

                if (fotoFilename == null || fotoFilename.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }

                Resource resource = imagemPerfilService.carregarImagemLivro(fotoFilename);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/editarPost/{id}")
    public ResponseEntity<String> editarPost(@RequestParam String token, 
                                              @PathVariable Long id,
                                              @RequestBody PostRequestDTO postRequestDTO) {
        String result = authService.editarPost(token, id, postRequestDTO);
        if (result.equals("Post editado com sucesso.")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Você não tem permissão para editar este post.") || 
                   result.equals("Usuário não está logado.") || 
                   result.equals("Post não encontrado.")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
