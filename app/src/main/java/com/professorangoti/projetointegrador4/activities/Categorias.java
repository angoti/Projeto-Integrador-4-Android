package com.professorangoti.projetointegrador4.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.professorangoti.projetointegrador4.CategoriaAdapter;
import com.professorangoti.projetointegrador4.Dados;
import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Categoria;

public class Categorias extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        exibeDados();
    }

    public void showTitle() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void exibeDados() {
        Categoria categoria = Dados.getInstanciaUnica().getCategoria();
        ListView listView = (ListView) findViewById(R.id.listview_categorias);
        if (categoria.getProdutos().size() == 0) {
            //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Arrays.asList("Vazio")));
            listView.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.list_empty_categorias)).setText("Vazio");
        } else {
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new CategoriaAdapter(this, categoria.getProdutos()));
        }
    }
}
