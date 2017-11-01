package com.professorangoti.projetointegrador4.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Categoria;
import com.professorangoti.projetointegrador4.domain.Produto;
import com.professorangoti.projetointegrador4.services.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProdutoForm extends AppCompatActivity {

    List<Categoria> listaCategoriasDoProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        montaListaDeCategorias();
    }

    private void montaListaDeCategorias() {
        listaCategoriasDoProduto = new ArrayList<Categoria>();
        final ListView listviewCategorias = (ListView) findViewById(R.id.listaCategorias);
        listviewCategorias.setAdapter(new ArrayAdapter<Categoria>(this, android.R.layout.simple_list_item_checked, MainActivity.getListaCategorias()));
        listviewCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                Log.i("teste", posicao + "");
                if (listaCategoriasDoProduto.contains(MainActivity.getListaCategorias().get(posicao)))
                    listaCategoriasDoProduto.remove(MainActivity.getListaCategorias().get(posicao));
                else
                    listaCategoriasDoProduto.add(MainActivity.getListaCategorias().get(posicao));
            }
        });
    }

    public void cadastrar(View v){
        final ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.VISIBLE);
        Log.i("teste",((EditText)findViewById(R.id.precoProduto)).getText().toString());
        Double preco = Double.parseDouble( ((EditText)findViewById(R.id.precoProduto)).getText().toString());
        String nome = ((EditText)findViewById(R.id.nomeProduto)).getText().toString();
        String url = ((EditText)findViewById(R.id.urlImagem)).getText().toString();
        Produto produto = new Produto(null,nome,preco,url);
        produto.setCategorias(listaCategoriasDoProduto);

        for(Categoria categoria : listaCategoriasDoProduto)
            categoria.setProdutos(null);

        RetrofitService.getServico().salvarProduto(produto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb.setVisibility(View.GONE);
                Toast.makeText(ProdutoForm.this, "Produto salvo no banco de dados. "+response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Log.i("teste",t.getMessage());
            }
        });
    }
}
