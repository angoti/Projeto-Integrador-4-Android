package com.professorangoti.projetointegrador4.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.professorangoti.projetointegrador4.CategoriaAdapter;
import com.professorangoti.projetointegrador4.EndPoint;
import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Categoria;
import com.professorangoti.projetointegrador4.domain.Produto;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String baseUrl = "https://gs-sts-cloud-foundry-deployment-angoti.cfapps.io/";
    private EndPoint apiService;
    private List<Categoria> listaCategorias;
    private ListView listviewProdutos, listviewCategorias;
    private TextView texto;
    private ImageView imagem;
    private float transparencia = 0.1f;
    private Context contexto;
    private SwipeActionAdapter mAdapter;
    Categoria categoria;

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
        contexto = this;
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
        } else if (id == R.id.op2) {
            transparencia = 0.1f;
            imagem.setAlpha(transparencia);
            return true;
        } else if (id == R.id.op3) {
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
            webServiceTodasCategorias();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void webServiceCategoriaPorId(int id) {
        Call<Categoria> call = apiService.getCategoria(id);
        //chamada assíncrona
        call.enqueue(new Callback<Categoria>() {
            @Override
            public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                int statusCode = response.code();
                categoria = response.body();
                exibeDados(categoria);
            }

            @Override
            public void onFailure(Call<Categoria> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void webServiceTodasCategorias() {
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
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    // Exibe na ListView todos os produtos de uma categoria
    // linha.xml define o layout de cada linha no Listview
    private void exibeDados(final Categoria categoria) {
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
            final List<Produto> listaProdutos = categoria.getProdutos();

            // 1. Wrap the Adapter of your ListView with a SwipeActionAdapter
            // 2. Create a background layout for each swipe direction you wish to act upon.
            // 3. Implement the SwipeActionAdapter

            // Wrap your content in a SwipeActionAdapter
            mAdapter = new SwipeActionAdapter(new CategoriaAdapter(this, listaProdutos));

            // Pass a reference of your ListView to the SwipeActionAdapter
            mAdapter.setListView(listviewProdutos);

            // Set the SwipeActionAdapter as the Adapter for your ListView
            //setListAdapter(mAdapter);
            listviewProdutos.setAdapter(mAdapter);

            mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);

            // Listen to swipes
            mAdapter.setSwipeActionListener(
                    new SwipeActionAdapter.SwipeActionListener() {
                        @Override
                        public boolean hasActions(int position, SwipeDirection direction) {
                            return true;
                        }

                        @Override
                        public boolean shouldDismiss(int position, SwipeDirection direction) {
                            return true;
                        }

                        @Override
                        public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
                            int id = listaProdutos.get(positionList[0]).getId();
                            listaProdutos.remove(positionList[0]);
                            mAdapter.notifyDataSetChanged();
                            webServiceRemoveProduto(id);
                        }
                    }
            );
        }
    }

    private void webServiceRemoveProduto(int i) {
        Call<ResponseBody> call = apiService.removeProduto(i);
        //chamada assíncrona
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int statusCode = response.code();
                Toast.makeText(MainActivity.this, "Produto removido da base de dados: "+statusCode, Toast.LENGTH_SHORT).show();
                webServiceCategoriaPorId(categoria.getId());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                webServiceCategoriaPorId(categorias.get(posicao).getId());
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

