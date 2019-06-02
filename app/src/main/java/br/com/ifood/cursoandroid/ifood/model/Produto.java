package br.com.ifood.cursoandroid.ifood.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class Produto {
    private String idUsuario, nome, descricao, imagemURL, idProduto;
    private Double preco;
    private boolean situacao;

    public Produto() {
        DatabaseReference produtosRef = ConfiguracaoFirebase.getFirebase()
                .child("produtos");
        setIdProduto(produtosRef.push().getKey());
    }

    public Produto(String idUsuario, String nome, String descricao, String imagemURL, Double preco) {
        this.idUsuario  = idUsuario;
        this.nome       = nome;
        this.descricao  = descricao;
        this.imagemURL  = imagemURL;
        this.preco      = preco;
        this.situacao   = true;
        DatabaseReference produtosRef = ConfiguracaoFirebase.getFirebase()
                .child("produtos");
        setIdProduto(produtosRef.push().getKey());
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getImagemURL() {
        return imagemURL;
    }

    public void setImagemURL(String imagemURL) {
        this.imagemURL = imagemURL;
    }

    public void salvar() {
        DatabaseReference empresaRef = ConfiguracaoFirebase.getFirebase()
                .child("produtos").child(idUsuario).child(getIdProduto());
        empresaRef.setValue(this);
    }

    public void mudarSituacao() {
        DatabaseReference dRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference prodRef = dRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        prodRef.child("situacao").setValue(!isSituacao());
    }

    @Override
    public String toString() {
        return "Produto{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", imagemURL='" + imagemURL + '\'' +
                ", idProduto='" + idProduto + '\'' +
                ", preco=" + preco +
                '}';
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public boolean isSituacao() {
        return situacao;
    }

    public void setSituacao(boolean situacao) {
        this.situacao = situacao;
    }
}
