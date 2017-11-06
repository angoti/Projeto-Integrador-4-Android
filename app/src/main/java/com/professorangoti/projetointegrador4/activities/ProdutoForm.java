package com.professorangoti.projetointegrador4.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.professorangoti.projetointegrador4.R;
import com.professorangoti.projetointegrador4.domain.Categoria;
import com.professorangoti.projetointegrador4.domain.Produto;
import com.professorangoti.projetointegrador4.services.RetrofitService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProdutoForm extends AppCompatActivity {

    List<Categoria> listaCategoriasDoProduto;

    //firebase
    private StorageReference mStorageRef;
    private String urlArquivoFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        montaListaDeCategorias();

        //firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void montaListaDeCategorias() {
        listaCategoriasDoProduto = new ArrayList<Categoria>();
        final ListView listviewCategorias = (ListView) findViewById(R.id.listaCategorias);
        listviewCategorias.setAdapter(new ArrayAdapter<Categoria>(this, android.R.layout.simple_list_item_checked, MainActivity.getListaCategorias()));
        listviewCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                if (listaCategoriasDoProduto.contains(MainActivity.getListaCategorias().get(posicao)))
                    listaCategoriasDoProduto.remove(MainActivity.getListaCategorias().get(posicao));
                else
                    listaCategoriasDoProduto.add(MainActivity.getListaCategorias().get(posicao));
            }
        });
    }

    public void cadastrar(View v) {
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.VISIBLE);
        Double preco = Double.parseDouble(((EditText) findViewById(R.id.precoProduto)).getText().toString());
        String nome = ((EditText) findViewById(R.id.nomeProduto)).getText().toString();
        String url = urlArquivoFirebase;
        Produto produto = new Produto(null, nome, preco, url);
        produto.setCategorias(listaCategoriasDoProduto);

        for (Categoria categoria : listaCategoriasDoProduto)
            categoria.setProdutos(null);

        RetrofitService.getServico().salvarProduto(produto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb.setVisibility(View.GONE);
                Toast.makeText(ProdutoForm.this, "Produto salvo no banco de dados. " + response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    public void escolherImagem(View v) {
        ((Button) findViewById(R.id.buttonCadastroProduto)).setEnabled(false);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ((ImageView) findViewById(R.id.imageViewImagem)).setImageBitmap(selectedImage);
                    uploadImagemParaFirebase(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadImagemParaFirebase(Uri file) {
        StorageReference riversRef = mStorageRef.child("images/" + getFileName(file));

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        urlArquivoFirebase = downloadUrl.toString();
                        ((Button) findViewById(R.id.buttonCadastroProduto)).setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ProdutoForm.this, "Erro: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Nullable
    private Uri reduzTamanhoImagem(Uri imagemUri) {
        String newPath = getRealPathFromURI(getApplicationContext(), imagemUri);
        Bitmap bMap=null;

        try {
            bMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagemUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap out = Bitmap.createScaledBitmap(bMap, 150, 150, false);
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        File resizedFile = new File(imageStorageDir, "resize.png");
        try {
            resizedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream fOut = null;
        try {
            final FileOutputStream out1 = new FileOutputStream(resizedFile);
            fOut = new BufferedOutputStream(out1);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            bMap.recycle();
            out.recycle();
        } catch (Exception e) { // TODO
            e.printStackTrace();
        }
        return Uri.fromFile(resizedFile);
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
