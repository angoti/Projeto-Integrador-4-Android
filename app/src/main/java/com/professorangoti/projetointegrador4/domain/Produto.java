package com.professorangoti.projetointegrador4.domain;

import java.util.ArrayList;
import java.util.List;

public class Produto {
	private Integer id;
	private String nome;
	private Double preco;
	private List<Categoria> categorias = new ArrayList<Categoria>();
	private String imagemURL;

	public Produto(Integer id, String nome, Double preco) {
		super();
		this.id = id;
		this.nome = nome;
		this.preco = preco;
	}

	public Produto(Integer id, String nome, Double preco, String imagemURL) {
		super();
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.imagemURL = imagemURL;
	}

	public Produto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public String getImagemURL() {
		return imagemURL;
	}

	public void setImagemURL(String imagemURL) {
		this.imagemURL = imagemURL;
	}
}
