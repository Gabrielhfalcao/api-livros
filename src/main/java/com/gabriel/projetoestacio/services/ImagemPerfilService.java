package com.gabriel.projetoestacio.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    
    @Value("${upload.path}") 
    private String uploadPath;

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

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        
        Files.copy(file.getInputStream(), Paths.get(uploadPath, fileName), StandardCopyOption.REPLACE_EXISTING);

        usuario.setImagemPerfil(fileName);
        usuarioRepository.save(usuario);

        if (imagemAntiga != null && !imagemAntiga.isEmpty()) {
            Path path = Paths.get(uploadPath, imagemAntiga);
            Files.deleteIfExists(path);
        }
        
        return fileName;
    }

    public String salvarImagemLivro(MultipartFile file, String existingFileName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Falha ao armazenar imagem vazia.");
        }

        if (existingFileName != null && !existingFileName.isEmpty()) {
            Path existingFilePath = Paths.get(uploadPath, existingFileName);
            Files.deleteIfExists(existingFilePath);
        }

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(uploadPath, filename), StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    public void apagarImagem(String filename) throws IOException {
        if (filename != null && !filename.isEmpty()) {
            Path filePath = Paths.get(uploadPath, filename);
            Files.deleteIfExists(filePath);
        }
    }

    public Resource carregarImagemPerfil(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Não foi possível ler o arquivo de imagem: " + fileName);
        }
    }
    
    public Resource carregarImagemLivro(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Não foi possível ler o arquivo de imagem: " + fileName);
        }
    }
}
