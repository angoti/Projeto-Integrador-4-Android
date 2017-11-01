package com.professorangoti.projetointegrador4.services;

import com.professorangoti.projetointegrador4.domain.Categoria;
import com.professorangoti.projetointegrador4.domain.Produto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InterfaceDeServicos {
    @GET("/categorias/{id}")
    Call<Categoria> getCategoria(@Path("id") int id);

    @GET("/categorias")
    Call<List<Categoria>> getCategorias();

    @DELETE("/produtos/{id}")
    Call<ResponseBody> removeProduto(@Path("id") int id);

    @POST("/categorias")
    Call<ResponseBody> salvar(@Body Categoria categoria);

    @POST("/produtos")
    Call<ResponseBody> salvarProduto(@Body Produto produto);
}

