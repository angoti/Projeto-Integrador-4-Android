package com.professorangoti.categorias.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.professorangoti.categorias.CategoriaAdapter;
import com.professorangoti.categorias.Dados;
import com.professorangoti.categorias.EndPoint;
import com.professorangoti.categorias.R;
import com.professorangoti.categorias.activities.Categorias;
import com.professorangoti.categorias.domain.Categoria;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String baseUrl = "https://gs-sts-cloud-foundry-deployment-angoti.cfapps.io/";
    EndPoint apiService;
    List<Categoria> listaCategorias;
    ListView listviewProdutos, listviewCategorias;
    TextView texto;
    ImageView imagem;
    float transparencia=0.1f;

    public List<Categoria> getListaCategorias() {
        return listaCategorias;
    }

    public void setListaCategorias(List<Categoria> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        apiService = criaRetrofit().create(EndPoint.class);
        listviewCategorias = (ListView) findViewById(R.id.listview_categorias);
        listviewProdutos = (ListView) findViewById(R.id.listview_produtos);
        texto = findViewById(R.id.lista_vazia);
        imagem = findViewById(R.id.imagem);
        escondeListviewProdutos();
        escondeListviewCategorias();
    }

    @Override
    public void onBackPressed() {
        setTitle("Sistema CatProd");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (
                (listviewCategorias.getVisibility() == View.GONE && listviewProdutos.getVisibility() == View.VISIBLE)
                        || (texto.getVisibility() == View.VISIBLE)) {
            escondeListviewProdutos();
            mostraListviewCategorias();
            setTitle("Categorias");
            escondeTexto();
        } else if (listviewCategorias.getVisibility() == View.VISIBLE) {
            escondeListviewCategorias();
            imagem.setAlpha(1f);
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.op1) {
            transparencia = 0.0f;
            imagem.setAlpha(transparencia);
            return true;
        }else if (id == R.id.op2) {
            transparencia = 0.1f;
            imagem.setAlpha(transparencia);
            return true;
        }else if (id == R.id.op3) {
            transparencia = 0.5f;
            imagem.setAlpha(transparencia);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ((TextView) findViewById(R.id.lista_vazia)).setVisibility(View.GONE);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cat) {
            consultaCategorias();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void consultaCategoriaId(int id) {
        Call<Categoria> call = apiService.getCategoria(id);
        //chamada assíncrona
        call.enqueue(new Callback<Categoria>() {
            @Override
            public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                int statusCode = response.code();
                Categoria categoria = response.body();
                exibeDados(categoria);
            }

            @Override
            public void onFailure(Call<Categoria> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    private void consultaCategorias() {
        if (listaCategorias != null) {
            exibeDados(listaCategorias);
            return;
        }
        Call<List<Categoria>> call = apiService.getCategorias();
        //chamada assíncrona
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                int statusCode = response.code();
                listaCategorias = response.body();
                exibeDados(listaCategorias);
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    @NonNull
    private Retrofit criaRetrofit() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void exibeDados(Categoria categoria) { //lista todos os produtos de uma categoria
        // Dados.getInstanciaUnica().setCategoria(categoria);
        // Intent i = new Intent(this, Categorias.class);
        // startActivity(i);
        setTitle("Lista de produtos");
        if (categoria.getProdutos().size() == 0) {
            escondeListviewProdutos();
            escondeListviewCategorias();
            mostraTexto();
            texto.setText("Vazio");
        } else {
            escondeTexto();
            escondeListviewCategorias();
            mostraListviewProdutos();
            listviewProdutos.setAdapter(new CategoriaAdapter(this, categoria.getProdutos()));
        }
    }

    private void exibeDados(final List<Categoria> categorias) { //lista todas as categorias
        setTitle("Categorias");
        escondeListviewProdutos();
        escondeTexto();
        mostraListviewCategorias();
        listviewCategorias.setAdapter(new ArrayAdapter<Categoria>(this, android.R.layout.simple_list_item_1, categorias));
        listviewCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                consultaCategoriaId(categorias.get(posicao).getId());
            }
        });
    }

    public void escondeListviewProdutos() {
        listviewProdutos.setVisibility(View.GONE);
    }

    public void escondeListviewCategorias() {
        listviewCategorias.setVisibility(View.GONE);
    }

    public void mostraListviewProdutos() {
        imagem.setAlpha(transparencia);
        listviewProdutos.setVisibility(View.VISIBLE);
    }

    public void mostraListviewCategorias() {
        imagem.setAlpha(transparencia);
        listviewCategorias.setVisibility(View.VISIBLE);
    }

    public void escondeTexto() {
        texto.setVisibility(View.GONE);
    }

    public void mostraTexto() {
        imagem.setAlpha(transparencia);
        texto.setVisibility(View.VISIBLE);
    }
}

