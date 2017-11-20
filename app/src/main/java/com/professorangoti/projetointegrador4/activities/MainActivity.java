package com.professorangoti.projetointegrador4.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Categoria;
import com.professorangoti.projetointegrador4.domain.CategoriaAdapter;
import com.professorangoti.projetointegrador4.domain.Produto;
import com.professorangoti.projetointegrador4.services.RetrofitService;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Categoria categoria;
    private static List<Categoria> listaCategorias;

    private ListView listviewProdutos, listviewCategorias;
    private TextView texto;
    private ImageView imagem;
    private FloatingActionButton fab;

    private float transparencia = 0.1f;
    private Context contexto;
    private SwipeActionAdapter mAdapter;


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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listviewCategorias = (ListView) findViewById(R.id.listview_categorias);
        listviewProdutos = (ListView) findViewById(R.id.listview_produtos);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        texto = (TextView) findViewById(R.id.lista_vazia);
        imagem = (ImageView) findViewById(R.id.imagem);

        escondeListviewProdutos();
        escondeListviewCategorias();
    }

    @Override
    public void onBackPressed() {
        setTitle(getResources().getString(R.string.app_name));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (
                (listviewCategorias.getVisibility() == View.GONE && listviewProdutos.getVisibility() == View.VISIBLE)
                        || (texto.getVisibility() == View.VISIBLE)) {
            escondeListviewProdutos();
            mostraListviewCategorias();
            setTitle("Categorias");
            escondeTexto();
            fab.setVisibility(View.VISIBLE);
        } else if (listviewCategorias.getVisibility() == View.VISIBLE) {
            escondeListviewCategorias();
            fab.setVisibility(View.GONE);
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
        RetrofitService.getServico().getCategoria(id).enqueue(new Callback<Categoria>() {
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
        RetrofitService.getServico().getCategorias().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                listaCategorias = response.body();
                exibeDados(listaCategorias);
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void webServiceRemoveProduto(int i) {
        RetrofitService.getServico().removeProduto(i).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int statusCode = response.code();
                Toast.makeText(MainActivity.this, "Produto removido da base de dados: " + statusCode, Toast.LENGTH_SHORT).show();
                webServiceCategoriaPorId(categoria.getId());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Exibe na ListView todos os produtos de uma categoria
    // linha.xml define o layout de cada linha no Listview
    private void exibeDados(final Categoria categoria) {
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent novoProduto = new Intent(MainActivity.this, ProdutoForm.class);
                startActivity(novoProduto);
            }
        });

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

            mAdapter = new SwipeActionAdapter(new CategoriaAdapter(this, listaProdutos));
            mAdapter.setListView(listviewProdutos);
            listviewProdutos.setAdapter(mAdapter);
            mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent novaCategoria = new Intent(MainActivity.this, CategoriaForm.class);
                startActivity(novaCategoria);
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

    public static List<Categoria> getListaCategorias() {
        return listaCategorias;
    }

    public static void limpaListaCategorias() {
        listaCategorias = null;
    }
}

