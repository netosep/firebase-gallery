package com.example.firebasegallery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasegallery.R;
import com.example.firebasegallery.adapter.ImagemAdapter;
import com.example.firebasegallery.model.Imagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private ConstraintLayout emptyLayout;
    private RecyclerView recyclerView;
    private ArrayList<Imagem> imagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        carregarComponentes();
        setTitle("Suas fotos");
        carregarGaleria();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_button, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnUpload) {
            startActivity(new Intent(GalleryActivity.this, UploadImagemActivity.class));
            finish();
        }
        return true;
    }

    private void carregarComponentes() {
        emptyLayout = findViewById(R.id.telaVazio);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void carregarGaleria() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imagens = new ArrayList<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("imagens");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    imagens.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Imagem imagem = snapshot.getValue(Imagem.class);
                        imagens.add(imagem);
                    }
                    recyclerView.setAdapter(new ImagemAdapter(getApplicationContext(), imagens));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseDatabase", error.getMessage());
            }
        });
    }
}