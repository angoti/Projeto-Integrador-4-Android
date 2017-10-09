package com.professorangoti.categorias;

import com.professorangoti.categorias.domain.Categoria;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EndPoint {
    @GET("/categorias/{id}")
    Call<Categoria> listaCategorias(@Path("id") int id);

}
