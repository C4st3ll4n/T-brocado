package br.com.ifood.cursoandroid.ifood.activity.empresa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class EmpresaActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        configurarLayout();
        configurarObjetos();
    }

    private void configurarObjetos() {
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tô brocado - Empresa");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empresa_menu,menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
