package br.com.ifood.cursoandroid.ifood.activity.usuario;

import android.app.Dialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stephenvinouze.materialnumberpickercore.MaterialNumberPicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.adapter.AdapterProduto;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.listener.RecyclerItemClickListener;
import br.com.ifood.cursoandroid.ifood.model.Empresa;
import br.com.ifood.cursoandroid.ifood.model.ItemPedido;
import br.com.ifood.cursoandroid.ifood.model.Pedido;
import br.com.ifood.cursoandroid.ifood.model.Produto;
import br.com.ifood.cursoandroid.ifood.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.ifood.cursoandroid.ifood.listener.RecyclerItemClickListener.*;

public class CardapioActivity extends AppCompatActivity {
    private RecyclerView rvCardapio;
    private List<Produto> cardapioList = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private AdapterProduto adapterCardapio = new AdapterProduto(cardapioList, this);
    private Toolbar toolbar;
    private Empresa empresa;

    private Pedido pedido;

    private CircleImageView civImagem;
    private TextView txtNomeEmpresa;

    private Usuario usuario;
    private String ID_USUARIO;


    AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);
        recuperarDados();
        conigurarLayout();
        configurarObjetos();
        recuperarCardapio();
        recuperarDadpsUsuario();
    }

    private void recuperarDadpsUsuario() {

        ad.show();

        DatabaseReference dRef = ConfiguracaoFirebase.getFirebase().child("usuarios")
                .child(ID_USUARIO);

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido() {
        ad.dismiss();
        DatabaseReference dRef = ConfiguracaoFirebase.getFirebase().child("pedidos");
    }

    private void recuperarDados() {

        empresa = (Empresa) getIntent().getExtras().getSerializable("empresa");
        ID_USUARIO = ConfiguracaoFirebase.getIdUsuario();
    }

    private void recuperarCardapio() {
     ad.show();
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebase().child("produtos")
                .child(empresa.getIdUsuario());

        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardapioList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getValue() != null) {
                        Produto p = ds.getValue(Produto.class);
                        /*Toast.makeText(CardapioActivity.this, "P>"+p.getNome(), Toast.LENGTH_SHORT).show();*/
                        if (p.isSituacao())cardapioList.add(p);
                    }
                }
                adapterCardapio.notifyDataSetChanged();
                ad.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void conigurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        toolbar.setSubtitle(empresa.getNome());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvCardapio = findViewById(R.id.rvCardapio);
        rvCardapio.setHasFixedSize(true);
        rvCardapio.setLayoutManager(new LinearLayoutManager(this));
        rvCardapio.setAdapter(adapterCardapio);
        rvCardapio.addOnItemTouchListener(new RecyclerItemClickListener(this, rvCardapio, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Produto p = cardapioList.get(position);
                for (ItemPedido i: itensCarrinho){
                    if (i.getIdProduto().equals(p.getIdProduto())){
                        Toast.makeText(CardapioActivity.this, "i", Toast.LENGTH_SHORT).show();
                        perguntarQuantidade(position,i);
                    }
                }
                perguntarQuantidade(position,null);
            }

            @Override
            public void onLongItemClick(View view, int position) {}

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { }
        }));

        civImagem = findViewById(R.id.civCompanyImage);
        txtNomeEmpresa = findViewById(R.id.txtCompanyName);

        Picasso.get().load(empresa.getUrlImagem()).into(civImagem);
        txtNomeEmpresa.setText(empresa.getNome());

        ad = new AlertDialog.Builder(this)
                .setView(R.layout.carregando).setCancelable(false).create();

    }

    private void confirmarQuantidade(int position, int quantidade) {
        Produto p = cardapioList.get(position);
        ItemPedido ip = new ItemPedido(p.getIdProduto(), p.getNome(), p.getPreco(), quantidade);
       /* Toast.makeText(this, "AQUI", Toast.LENGTH_SHORT).show();

        for (ItemPedido itemP: itensCarrinho){
            if (itensCarrinho.contains(itemP)){
                Toast.makeText(this, "Existe", Toast.LENGTH_SHORT).show();
            }else if (!itensCarrinho.contains(itemP)){
                Toast.makeText(this, "ÑExiste", Toast.LENGTH_SHORT).show();
                itensCarrinho.add(ip);
            }
        }*/

        itensCarrinho.add(ip);

        if (pedido == null) pedido = new Pedido(empresa.getIdUsuario(),usuario.getIdUsuario());

        pedido.setNome(usuario.getNome());
        pedido.setEndereco(usuario.getEndereco());
        pedido.setItens(itensCarrinho);
        pedido.salvar();
    }

    private void configurarObjetos(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_confirmar_pedido){
            finalizarPedido();
        }
        return super.onOptionsItemSelected(item);
    }

    private void finalizarPedido() {

    }

    private void perguntarQuantidade(int position, @Nullable ItemPedido ip){
        Dialog adb = new Dialog(CardapioActivity.this);
        adb.setContentView(R.layout.quantidade_pedido);

        Button btOk, btCancelar;
        btOk = adb.findViewById(R.id.btOk);
        btCancelar = adb.findViewById(R.id.btCancelar);
        MaterialNumberPicker numberPicker = adb.findViewById(R.id.materialNumberPicker);

      /**  if (ip != null){
            numberPicker.setValue(ip.getQuantidade());
            int novaQuantidade = numberPicker.getValue();
            btOk.setOnClickListener((l)->{
             atualizarQuantidade(ip, novaQuantidade);
                adb.dismiss();
            });
        }else{*/
            btOk.setOnClickListener((l)-> {
                confirmarQuantidade(position, numberPicker.getValue());
                        adb.dismiss();
            });
    //    }
                btCancelar.setOnClickListener((l)-> adb.dismiss());

        adb.create();
        adb.show();
    }

    private void atualizarQuantidade(ItemPedido ip, int novaQuantidade) {
        ip.setQuantidade(novaQuantidade);
    }
}
