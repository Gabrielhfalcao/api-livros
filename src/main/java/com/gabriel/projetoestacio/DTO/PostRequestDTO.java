package com.gabriel.projetoestacio.DTO;

public class PostRequestDTO {

	private String token;
	private String descricao;
	private String tituloLivro;
	private String autorLivro;
	private String idiomaLivro;
	private String categoriaLivro;

	public PostRequestDTO() {
		super();
	}

	public PostRequestDTO(String token, String descricao, String tituloLivro, String autorLivro, String idiomaLivro,
			String categoriaLivro) {
		super();
		this.token = token;
		this.descricao = descricao;
		this.tituloLivro = tituloLivro;
		this.autorLivro = autorLivro;
		this.idiomaLivro = idiomaLivro;
		this.categoriaLivro = categoriaLivro;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTituloLivro() {
		return tituloLivro;
	}

	public void setTituloLivro(String tituloLivro) {
		this.tituloLivro = tituloLivro;
	}

	public String getAutorLivro() {
		return autorLivro;
	}

	public void setAutorLivro(String autorLivro) {
		this.autorLivro = autorLivro;
	}

	public String getIdiomaLivro() {
		return idiomaLivro;
	}

	public void setIdiomaLivro(String idiomaLivro) {
		this.idiomaLivro = idiomaLivro;
	}

	public String getCategoriaLivro() {
		return categoriaLivro;
	}

	public void setCategoriaLivro(String categoriaLivro) {
		this.categoriaLivro = categoriaLivro;
	}

}
