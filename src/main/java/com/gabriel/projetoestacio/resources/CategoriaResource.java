package com.gabriel.projetoestacio.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabriel.projetoestacio.entities.Categoria;
import com.gabriel.projetoestacio.services.CategoriaService;

@RestController
@RequestMapping(value = "api/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaService categoriaService;
	
	public ResponseEntity<List<Categoria>> findAll() {
		List<Categoria> categorias = categoriaService.findAll();
		return ResponseEntity.ok().body(categorias);
	}
	
}
