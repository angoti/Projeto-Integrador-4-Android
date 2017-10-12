package com.professorangoti.categorias;


import com.professorangoti.categorias.domain.Categoria;

public class Dados {
    private static Dados instanciaUnica;
    private Categoria categoria;

    private Dados() {}

    public static Dados getInstanciaUnica(){
        if(instanciaUnica==null)
            instanciaUnica = new Dados();
        return instanciaUnica;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
