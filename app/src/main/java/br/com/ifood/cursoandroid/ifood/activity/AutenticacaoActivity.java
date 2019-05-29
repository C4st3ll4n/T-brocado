package br.com.ifood.cursoandroid.ifood.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.activity.empresa.EmpresaActivity;
import br.com.ifood.cursoandroid.ifood.activity.usuario.HomeActivity;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.helper.UsuarioFirebase;

public class AutenticacaoActivity extends Activity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;

    private FirebaseAuth autenticacao;
    private LinearLayout linearTipoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);
        getActionBar().hide();

        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        //Verificar usuario logado
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(this::check);

        botaoAcessar.setOnClickListener(v -> {

            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {

                    //Verifica estado do switch
                    if (tipoAcesso.isChecked()) {//Cadastro

                        autenticacao.createUserWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(AutenticacaoActivity.this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                                UsuarioFirebase.atualizarTipoUsuario(getTipoUser());
                                abrirTelaPrincipal(getTipoUser());

                            } else {

                                String erroExcecao = "";

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    erroExcecao = "Digite uma senha mais forte!";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    erroExcecao = "Por favor, digite um e-mail válido";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    erroExcecao = "Este conta já foi cadastrada";
                                } catch (Exception e) {
                                    erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                    e.printStackTrace();
                                }

                                Toast.makeText(AutenticacaoActivity.this,
                                        "Erro: " + erroExcecao,
                                        Toast.LENGTH_SHORT).show();

                            }

                        });

                    } else {//Login

                        autenticacao.signInWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                Toast.makeText(AutenticacaoActivity.this,
                                        "Logado com sucesso",
                                        Toast.LENGTH_SHORT).show();
                                abrirTelaPrincipal(task.getResult().getUser().getDisplayName());

                            } else {
                                Toast.makeText(AutenticacaoActivity.this,
                                        "Erro ao fazer login : " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AutenticacaoActivity.this,
                        "Preencha o E-mail!",
                        Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void check(CompoundButton compoundButton, boolean b) {
        if (b) {
            linearTipoUser.setVisibility(View.VISIBLE);
        } else {
            linearTipoUser.setVisibility(View.VISIBLE);
        }
    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null) {
            abrirTelaPrincipal(usuarioAtual.getDisplayName());
        }
    }

    private void abrirTelaPrincipal(String tipoUser) {
        if (tipoUser.equals("E")) startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        else startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private void inicializaComponentes() {
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        linearTipoUser = findViewById(R.id.linearlTipoUsuario);
    }

    private String getTipoUser() {
        return tipoAcesso.isChecked() ? "E" : "U";
    }

}
