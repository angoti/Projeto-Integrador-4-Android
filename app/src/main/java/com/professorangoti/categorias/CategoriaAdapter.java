package com.professorangoti.categorias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.professorangoti.categorias.domain.Categoria;
import com.professorangoti.categorias.domain.Produto;

import java.util.List;


class CategoriaAdapter extends BaseAdapter {
    Context ctx;
    List<Produto> lista;

    public CategoriaAdapter(Context ctx, List<Produto> lista) {
        this.ctx = ctx;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //1º passo
        Produto produto = lista.get(i);

        //2º passo
        View linha = LayoutInflater.from(ctx).inflate(R.layout.linha,null);

        //3º passo
        TextView nome = (TextView) linha.findViewById(R.id.nome);
        TextView preco = (TextView) linha.findViewById(R.id.preco);

        nome.setText(produto.getNome());
        preco.setText(produto.getPreco()+"");

        return linha;
    }
}