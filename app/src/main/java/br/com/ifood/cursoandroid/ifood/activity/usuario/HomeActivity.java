package br.com.ifood.cursoandroid.ifood.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        configurarLayout();
        configurarObjetos();
    }

    private void configurarObjetos() {
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tô brocado - Usuário");
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search_view);
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
