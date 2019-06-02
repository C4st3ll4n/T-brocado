package br.com.ifood.cursoandroid.ifood.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.adapter.AdapterEmpresa;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.listener.RecyclerItemClickListener;
import br.com.ifood.cursoandroid.ifood.listener.RecyclerItemClickListener.OnItemClickListener;
import br.com.ifood.cursoandroid.ifood.model.Empresa;

import static android.support.v7.widget.LinearLayoutManager.*;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private MaterialSearchView searchView;
    private RecyclerView rvEmpresas;
    private List<Empresa> empresaList = new ArrayList<>();
    private AdapterEmpresa adapterEmpresa = new AdapterEmpresa(empresaList);
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        configurarLayout();
        configurarObjetos();
        recuperarEmpresas();
    }

    private void recuperarEmpresas() {
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empresaList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresaList.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void configurarObjetos() {
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tô brocado - Usuário");
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search_view);
        searchView.setHint("Pesquisar restaurante");

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                pesquisarEmpresas(newText);
                return true;
            }
        });
/*
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });*/

        rvEmpresas = findViewById(R.id.recyclerEmpresas);
        rvEmpresas.setHasFixedSize(true);
        rvEmpresas.setLayoutManager(new LinearLayoutManager(this, VERTICAL,false));
        rvEmpresas.setAdapter(adapterEmpresa);

        rvEmpresas.addOnItemTouchListener(new RecyclerItemClickListener(this, rvEmpresas, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(HomeActivity.this, CardapioActivity.class);
                i.putExtra("empresa", empresaList.get(position));
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }

    private void pesquisarEmpresas(String newText) {
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        Query q = empresaRef.orderByChild("nome").startAt(newText)
                .endAt(newText + "\uf8ff");

        q.addValueEventListener(new ValueEventListener() {
            @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    empresaList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        empresaList.add(ds.getValue(Empresa.class));
                    }
                    adapterEmpresa.notifyDataSetChanged();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usuario_menu, menu);

        MenuItem item = menu.findItem(R.id.menuUsuarioPesquisa);
        searchView.setMenuItem(item);

        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuUsuarioConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuUsuarioPesquisa:
                break;
            case R.id.menuUsuarioSair:
                deslogarUsuario();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes() {
        Intent i = new Intent(this, ConfiguracoesUsuarioActivity.class);
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
}
