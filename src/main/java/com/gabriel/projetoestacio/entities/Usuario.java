package com.gabriel.projetoestacio.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	private String email;
	private String usuario;
	private String senha;
	private String telefone;
	
	@JsonIgnore
    private String imagemPerfil;
	
    @JsonIgnore
	private String validationToken;
	
	@JsonIgnore
    private boolean emailValidated = false;
	
	@JsonIgnore
	@OneToMany
	private List<Post> publicacoes = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany
	private List<Post> favoritos = new ArrayList<>();
	
	public Usuario() {
		super();
	}

	public Usuario(Long id, String nome, String email, String usuario, String senha, String telefone,
			List<Post> publicacoes, List<Post> favoritos) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.usuario = usuario;
		this.senha = senha;
		this.telefone = telefone;
		this.publicacoes = publicacoes;
		this.favoritos = favoritos; 
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public List<Post> getPublicacoes() {
		return publicacoes;
	}

	public void addPost(Post post) {
		this.publicacoes.add(post);
	}

	public Long getId() {
		return id;
	}

	public List<Post> getFavoritos() {
		return favoritos;
	}

	public void addFavoritos(Post post) {
		this.favoritos.add(post);
	}

	public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }
    
	public String getImagemPerfil() {
		return imagemPerfil;
	}

	public void setImagemPerfil(String imagemPerfil) {
		this.imagemPerfil = imagemPerfil;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	} 
	
}
