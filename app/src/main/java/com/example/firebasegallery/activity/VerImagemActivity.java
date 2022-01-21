package com.example.firebasegallery.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasegallery.R;
import com.example.firebasegallery.model.Imagem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class VerImagemActivity extends AppCompatActivity {

    private ArrayList<Imagem> imagens;
    private ImageView imageView;
    private TextView nomeImagem;
    private Button btnExcluir, btnVoltar, btnLeft, btnRight;
    private int posicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagem);
        Objects.requireNonNull(getSupportActionBar()).hide();
        carregarComponentes();

        if(getIntent().hasExtra("imagens")) {
            posicao = (int) getIntent().getSerializableExtra("posicao");
            imagens = new ArrayList<>();
            int extraSize = getIntent().getParcelableArrayListExtra("imagens").size();
            for(int i = 0; i < extraSize; i++) {
                Imagem img = (Imagem) getIntent().getParcelableArrayListExtra("imagens").get(i);
                imagens.add(img);
            }
            Glide.with(this).load(imagens.get(posicao).getImagem()).into(imageView);
            nomeImagem.setText(imagens.get(posicao).getNome());
        }

        btnExcluir.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Deseja excluir essa imagem?");
            builder.setPositiveButton("Sim", (dialogInterface, i) -> {
                // excluindo referencia do storage database
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference desertRef = storageRef.child("imagens/" + imagens.get(posicao).getNome() + imagens.get(posicao).getUuId());
                desertRef.delete().addOnSuccessListener(unusedStorage -> {
                    // excluindo referencia do realtime database
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("imagens");
                    databaseRef.child(imagens.get(posicao).getUuId()).removeValue().addOnSuccessListener(unusedDatabase -> {
                        startActivity(new Intent(this, GalleryActivity.class));
                        Toast.makeText(this, "Imagem excluida com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(err -> {
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseDatabase", err.getMessage());
                    });
                }).addOnFailureListener(err -> {
                    Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseDatabase", err.getMessage());
                });

            });
            builder.setNegativeButton("Não", null);
            AlertDialog alert = builder.create();
            alert.show();
        });

        btnLeft.setOnClickListener(view -> {
            if (posicao - 1 >= 0) {
                posicao--;
                Glide.with(this).load(imagens.get(posicao).getImagem()).into(imageView);
                nomeImagem.setText(imagens.get(posicao).getNome());
            }
        });

        btnRight.setOnClickListener(view -> {
            if (posicao + 1 < imagens.size()) {
                posicao++;
                Glide.with(this).load(imagens.get(posicao).getImagem()).into(imageView);
                nomeImagem.setText(imagens.get(posicao).getNome());
            }
        });

        btnVoltar.setOnClickListener(view -> {
            startActivity(new Intent(VerImagemActivity.this, GalleryActivity.class));
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, GalleryActivity.class));
        finish();
    }

    private void carregarComponentes() {
        imageView = findViewById(R.id.imageView);
        nomeImagem = findViewById(R.id.textViewNomeImg);
        btnExcluir = findViewById(R.id.buttonExcluir);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
    }
}