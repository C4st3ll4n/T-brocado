package br.com.ifood.cursoandroid.ifood.activity.empresa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.adapter.AdapterProduto;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.listener.RecyclerItemClickListener;
import br.com.ifood.cursoandroid.ifood.model.Produto;

import static br.com.ifood.cursoandroid.ifood.listener.RecyclerItemClickListener.*;

public class EmpresaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rview;
    private List<Produto> produtoList = new ArrayList<>();
    private AdapterProduto adapterProduto = new AdapterProduto(produtoList, this);

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        configurarLayout();
        configurarObjetos();
        carregarProdutos();
    }

    private void configurarObjetos() {
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tô brocado - Empresa");
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);


        rview = findViewById(R.id.recyclerProdutos);
        rview.setHasFixedSize(true);
        rview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rview.setAdapter(adapterProduto);

        RecyclerItemClickListener clickListener = new RecyclerItemClickListener(this, rview,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Produto p = produtoList.get(position);
                        String titulo;
                        if (p.isSituacao()) titulo = "Desativar "+p.getNome()+" ?";
                        else titulo = "Reativar "+p.getNome()+" ?";
                        AlertDialog ab = new AlertDialog.Builder(EmpresaActivity.this)
                                .setTitle(titulo)
                                .setMessage("Tens certeza que quer quer trocar a situação desse produto?")
                                .setPositiveButton("Não", (dialogInterface, i) -> dialogInterface.dismiss())
                                .setNegativeButton("Sim", ((dialogInterface, i) ->{
                                    p.mudarSituacao();
                                    Toast.makeText(EmpresaActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();
                                    //carregarProdutos();
                                }))
                                .create();
                        ab.show();


                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });

        rview.addOnItemTouchListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empresa_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto:
                adicionarNovoProduto();
                break;
            case R.id.menuSair:
                deslogarUsuario();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes() {
        Intent i = new Intent(this, ConfiguracoesEmpresaActivity.class);
        startActivity(i);
    }

    private void adicionarNovoProduto() {
        Intent i = new Intent(this, NovoProdutoEmpresaActivity.class);
        startActivity(i);
    }

    private void deslogarUsuario() {
        try {
            firebaseAuth.signOut();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarProdutos() {
        Toast.makeText(this, "Carregando produtos...", Toast.LENGTH_LONG).show();
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebase().child("produtos")
                .child(ConfiguracaoFirebase.getIdUsuario());

        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtoList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getValue() != null) {
                        Produto p = ds.getValue(Produto.class);
                        /*if (p.isSituacao())*/produtoList.add(p);
                    }
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
