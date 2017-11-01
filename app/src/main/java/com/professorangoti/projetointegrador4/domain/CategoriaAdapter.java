package com.professorangoti.projetointegrador4.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Produto;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class CategoriaAdapter extends BaseAdapter {
    Context ctx;
    List<Produto> lista;

    public CategoriaAdapter(Context ctx, List<Produto> lista) {
        this.ctx = ctx;
        this.lista = lista;
    }

    @Override
    public void notifyDataSetChanged() {

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
        if (view == null) {
            //1º passo
            Produto produto = lista.get(i);

            //2º passo
            View linha = LayoutInflater.from(ctx).inflate(R.layout.linha, null);

            //3º passo
            TextView nome = (TextView) linha.findViewById(R.id.nome);
            TextView preco = (TextView) linha.findViewById(R.id.preco);
            ImageView imagem = (ImageView) linha.findViewById(R.id.imagem);

            nome.setText(produto.getNome());
            NumberFormat nf = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
            preco.setText("R$ " + nf.format(produto.getPreco()).toString());
            Picasso.with(ctx).load(produto.getImagemURL()).into(imagem);

            return linha;
        }
        return view;
    }
}