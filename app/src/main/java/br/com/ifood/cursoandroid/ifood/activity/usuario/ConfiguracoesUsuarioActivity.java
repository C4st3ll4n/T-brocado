package br.com.ifood.cursoandroid.ifood.activity.usuario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);
        configurarLayout();
        configurarObjetos();
    }

    private void configurarObjetos() {
        //firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações de Usuário");
        setSupportActionBar(toolbar);
    }

}
