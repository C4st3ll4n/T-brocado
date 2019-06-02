package br.com.ifood.cursoandroid.ifood.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.model.Produto;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jamilton
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    private final int ATIVO = 1;
    private final int DESATIVADO = 0;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public int getItemViewType(int position) {
        Produto produto = produtos.get(position);

        if (produto.isSituacao())return ATIVO;
        else return DESATIVADO;
//        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);

        if (getItemViewType(i) == DESATIVADO){
            holder.nome.setTextColor(Color.GRAY);
            holder.descricao.setTextColor(Color.GRAY);
            holder.valor.setTextColor(Color.GRAY);
        }else{
            holder.nome.setTextColor(Color.BLACK);
            holder.descricao.setTextColor(Color.BLACK);
            holder.valor.setTextColor(Color.GREEN);
        }
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$ " + produto.getPreco());
        Picasso.get().load(produto.getImagemURL()) .error(R.drawable.ic_kitchen_black_24dp).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;
        CircleImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
            image = itemView.findViewById(R.id.civProdutoImg);
        }
    }
}
