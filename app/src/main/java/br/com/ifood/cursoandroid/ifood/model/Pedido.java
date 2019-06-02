package br.com.ifood.cursoandroid.ifood.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class Pedido {
    private String idEmpresa, idUsuario, idPedido;
    private String endereco;
    private String nome;
    private String observacao;
    private Double total;
    private List<ItemPedido> itens;
    private String situacao = "pendente";
    private int metodoPagamento;

    public Pedido() {
    }

    public Pedido(String idEmpresa, String idUsuario) {
        this.idEmpresa = idEmpresa;
        this.idUsuario = idUsuario;

        DatabaseReference dref = ConfiguracaoFirebase.getFirebase().child("pedidos_usuario")
                .child(idEmpresa).child(idUsuario);

        setIdPedido(dref.push().getKey());

    }

    public Pedido(String idEmpresa, String idUsuario, String idPedido, String endereco,
                  String nome, String observacao, Double total, List<ItemPedido> itens,
                  String situacao, int metodoPagamento) {
        this.idEmpresa = idEmpresa;
        this.idUsuario = idUsuario;
        this.idPedido = idPedido;
        this.endereco = endereco;
        this.nome = nome;
        this.observacao = observacao;
        this.total = total;
        this.itens = itens;
        this.situacao = situacao;
        this.metodoPagamento = metodoPagamento;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void salvar(){

        DatabaseReference dref = ConfiguracaoFirebase.getFirebase().child("pedidos_usuario")
                .child(idEmpresa).child(idUsuario).child(idPedido);
        dref.setValue(this);
    }

}
