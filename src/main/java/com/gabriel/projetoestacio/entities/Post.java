package com.gabriel.projetoestacio.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Post implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Usuario usuario;
	
	@ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;
	private String descricao;
	
	private String fotoLivro1;
    private String fotoLivro2;
	
	public Post() {
		super();
	}

	public Post(Long id, Usuario usuario, Livro livro, String descricao, String fotoLivro1, String fotoLivro2) {
        this.id = id;
        this.usuario = usuario;
        this.livro = livro;
        this.descricao = descricao;
        this.fotoLivro1 = fotoLivro1;
        this.fotoLivro2 = fotoLivro2;
    }

	public String getFotoLivro1() {
		return fotoLivro1;
	}

	public void setFotoLivro1(String fotoLivro1) {
		this.fotoLivro1 = fotoLivro1;
	}

	public String getFotoLivro2() {
		return fotoLivro2;
	}

	public void setFotoLivro2(String fotoLivro2) {
		this.fotoLivro2 = fotoLivro2;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		Post other = (Post) obj;
		return Objects.equals(id, other.id);
	}
	
}
