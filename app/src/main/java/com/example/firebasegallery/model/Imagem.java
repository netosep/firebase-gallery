package com.example.firebasegallery.model;

import java.io.Serializable;

public class Imagem implements Serializable {

    private String uuId;
    private String nome;
    private String imagem;

    public Imagem() {}

    public Imagem(String nome, String imagem) {
        this.nome = nome;
        this.imagem = imagem;
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
