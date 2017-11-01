package com.professorangoti.projetointegrador4.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Categoria;
import com.professorangoti.projetointegrador4.services.RetrofitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaForm extends AppCompatActivity {
    EditText campo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campo = (EditText)findViewById(R.id.nomeCategoria);

        // força o aparecimento do teclado na tela
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void cadastrar(View v){
        final ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        RetrofitService.getServico().salvar(new Categoria(null,campo.getText().toString())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb.setVisibility(View.GONE);
                Toast.makeText(CategoriaForm.this, "Categoria cadastrada no banco de dados. "+response.message(), Toast.LENGTH_SHORT).show();
                MainActivity.limpaListaCategorias();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Log.i("teste",t.getMessage());
            }
        });
    }
}
