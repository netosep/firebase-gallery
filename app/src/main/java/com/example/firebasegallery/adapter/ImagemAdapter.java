package com.example.firebasegallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasegallery.R;
import com.example.firebasegallery.activity.VerImagemActivity;
import com.example.firebasegallery.model.Imagem;

import java.util.ArrayList;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.ViewHolder> {

    Context context;
    ArrayList<Imagem> imagens;

    public ImagemAdapter(Context context, ArrayList<Imagem> imagens) {
        this.context = context;
        this.imagens = imagens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Imagem imagem = imagens.get(position);
        viewHolder.nomeImagem.setText(imagem.getNome());
        viewHolder.imagens = imagens;
        Glide.with(context).load(imagens.get(position).getImagem()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nomeImagem;
        CardView cardImage;
        Context vhContext;
        ArrayList<Imagem> imagens;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgData);
            nomeImagem = itemView.findViewById(R.id.imgNome);
            cardImage = itemView.findViewById(R.id.cardImage);
            vhContext = itemView.getContext();

            cardImage.setOnClickListener(view -> {
                Intent intent = new Intent(vhContext, VerImagemActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("posicao", getAbsoluteAdapterPosition());
                intent.putExtra("imagens", imagens);
                vhContext.startActivity(intent);
            });

        }
    }
}
