package com.example.firebasegallery.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebasegallery.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UploadImagemActivity extends AppCompatActivity {

    private EditText nomeImagemEditText;
    private Button buttonBuscar, buttonEnviar;
    private ProgressBar progressBar;
    private ImageView imagePreview;
    private Uri caminhoImg;
    private String nomeImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        setTitle("Enviar imagem");
        iniciarComponentes();

        ActivityResultLauncher<Intent> intentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bitmap;
                        Uri imgSelecionada = result.getData().getData();

                        caminhoImg = imgSelecionada;
                        try {
                            // setando imagem de preview
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgSelecionada);
                            imagePreview.setImageBitmap(bitmap);
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
        );

        buttonBuscar.setOnClickListener(view -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentLauncher.launch(pickImage);
        });

        buttonEnviar.setOnClickListener(view -> {
            if (nomeImagemEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Nome da imagem vazio!", Toast.LENGTH_SHORT).show();
            } else if(caminhoImg == null) {
                Toast.makeText(this, "Nenhuma imagem selecionada!", Toast.LENGTH_SHORT).show();
            } else {
                nomeImagem = nomeImagemEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE); // mostrando progressbar
                buttonEnviar.setEnabled(false); // desabilitando botão enviar

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("imagens").push();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("imagens/" + nomeImagem + dbRef.getKey());

                storageRef.putFile(caminhoImg).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        dbRef.child("uuId").setValue(dbRef.getKey());
                        dbRef.child("nome").setValue(nomeImagem);
                        dbRef.child("imagem").setValue(Objects.requireNonNull(downloadUri).toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        buttonEnviar.setEnabled(true);
                        Toast.makeText(this, "Imagem enviada com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, GalleryActivity.class));
                        finish();
                    }
                }).addOnFailureListener(this, err -> {
                    Toast.makeText(this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    buttonEnviar.setEnabled(true);
                    Log.e("FirebaseDatabase", err.getMessage());
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, GalleryActivity.class));
        finish();
    }

    private void iniciarComponentes() {
        nomeImagemEditText = findViewById(R.id.editTextNomeImagem);
        buttonBuscar = findViewById(R.id.btnBuscar);
        buttonEnviar = findViewById(R.id.btnEnviar);
        imagePreview = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.proBarEnviandoImg);
    }
}