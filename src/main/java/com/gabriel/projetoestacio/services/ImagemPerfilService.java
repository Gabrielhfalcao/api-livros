package com.gabriel.projetoestacio.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gabriel.projetoestacio.entities.Usuario;
import com.gabriel.projetoestacio.exceptions.UnauthorizedException;
import com.gabriel.projetoestacio.repositories.UsuarioRepository;

@Service
public class ImagemPerfilService {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private S3Service s3Service;

    public String salvarImagemPerfil(MultipartFile file, String token) throws IOException {
        Optional<Long> usuarioLogadoOpt = authService.verificarUsuarioLogado(token);
        if (!usuarioLogadoOpt.isPresent()) {
            throw new UnauthorizedException("Usuário não está logado.");
        }

        Long usuarioLogadoId = usuarioLogadoOpt.get();
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioLogadoId);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        Usuario usuario = usuarioOpt.get();

        String imagemAntiga = usuario.getImagemPerfil();

        String fileName = s3Service.uploadFile(file);
        usuario.setImagemPerfil(fileName);
        usuarioRepository.save(usuario);

        if (imagemAntiga != null && !imagemAntiga.isEmpty()) {
            s3Service.deleteFile(imagemAntiga);
        }

        return fileName;
    }

    public String salvarImagemLivro(MultipartFile file, String existingFileName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Falha ao armazenar imagem vazia.");
        }

        if (existingFileName != null && !existingFileName.isEmpty()) {
            s3Service.deleteFile(existingFileName);
        }

        return s3Service.uploadFile(file);
    }

    public void apagarImagem(String filename) {
        s3Service.deleteFile(filename);
    }

    public Resource carregarImagemPerfil(String fileName) throws MalformedURLException {
        return s3Service.downloadFile(fileName);
    }

    public Resource carregarImagemLivro(String fileName) throws MalformedURLException {
        return s3Service.downloadFile(fileName);
    }
}
