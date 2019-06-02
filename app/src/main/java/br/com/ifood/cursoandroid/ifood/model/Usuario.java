package br.com.ifood.cursoandroid.ifood.model;

import com.google.firebase.database.DatabaseReference;

import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class Usuario {
    private String nome, endereco, contato, urlImagem, idUsuario;

    public Usuario() {
    }

    public Usuario(String nome, String endereco, String contato, String urlImagem, String idUsuario) {
        this.nome = nome;
        this.endereco = endereco;
        this.contato = contato;
        this.urlImagem = urlImagem;
        this.idUsuario = idUsuario;
    }

    public void salvar(){
        DatabaseReference empresaRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios").child(getIdUsuario());

        empresaRef.setValue(this);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
