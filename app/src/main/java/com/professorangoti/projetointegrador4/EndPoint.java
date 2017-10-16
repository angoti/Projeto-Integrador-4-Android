package com.professorangoti.projetointegrador4;

import com.professorangoti.projetointegrador4.domain.Categoria;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EndPoint {
    @GET("/categorias/{id}")
    Call<Categoria> getCategoria(@Path("id") int id);

    @GET("/categorias")
    Call<List<Categoria>> getCategorias();

    @DELETE("/produtos/{id}")
    Call<ResponseBody> removeProduto(@Path("id") int id);
}

