package br.com.ifood.cursoandroid.ifood.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class Empresa implements Serializable {
    private String nome, idUsuario, urlImagem, tempo, categoria;
    private Double taxaEntrega;

    public Empresa() {
    }

    public Empresa(String nome, String idUsuario, String urlImagem, String tempo, String categoria, Double taxaEntrega) {
        this.nome           = nome;
        this.idUsuario      = idUsuario;
        this.urlImagem      = urlImagem;
        this.tempo          = tempo;
        this.categoria      = categoria;
        this.taxaEntrega    = taxaEntrega;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


    public void salvar() {
        DatabaseReference empresaRef = ConfiguracaoFirebase.getFirebase()
                .child("empresas").child(getIdUsuario());

        empresaRef.setValue(this);
    }

    public Double getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(Double taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }
}
