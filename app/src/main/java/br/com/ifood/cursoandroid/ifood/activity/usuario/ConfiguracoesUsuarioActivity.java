package br.com.ifood.cursoandroid.ifood.activity.usuario;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.model.Empresa;
import br.com.ifood.cursoandroid.ifood.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private static final int SELECAO_GALERIA = 200;
    private static String URL_IMAGEM;
    private static String ID_USUARIO_LOGADO;

    private EditText etNome, etContato, etEndereco;
    private CircleImageView civImagemUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);
        configurarLayout();
        configurarObjetos();
        recuperarDadosUsuario();
    }

    private void configurarObjetos() {
        //firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        ID_USUARIO_LOGADO = ConfiguracaoFirebase.getIdUsuario();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações de Usuário");
        setSupportActionBar(toolbar);

        civImagemUsuario = findViewById(R.id.civImagemUsuario);
        civImagemUsuario.setOnClickListener(view -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )  ;

            if (i.resolveActivity(getPackageManager()) != null){
                startActivityForResult(i, SELECAO_GALERIA);
            }
        });

        etNome      = findViewById(R.id.etNomeUsuario);
        etContato   = findViewById(R.id.etContatoUsuario);
        etEndereco  = findViewById(R.id.etEnderecoUsuario);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap img = null;

            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri local = data.getData();
                        img       = MediaStore.Images.Media.getBitmap(getContentResolver(), local);
                        break;
                }

                if (img != null){
                    civImagemUsuario.setImageBitmap(img);
                    Toast.makeText(this,"Fazendo o upload da imagem...",
                            Toast.LENGTH_LONG).show();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] dadosImg = outputStream.toByteArray();

                    ConfiguracaoFirebase.getFirebaseStorage()
                            .child("imagens")
                            .child("usuarios")
                            .child(ID_USUARIO_LOGADO+"jpeg")
                            .putBytes(dadosImg)
                            .addOnFailureListener(e -> Toast.makeText(this,
                                    "Erro ao fazer o upload da imagem, tente novamente",
                                    Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener((v)-> {
                                Toast.makeText(this,
                                        "Sucesso no upload", Toast.LENGTH_SHORT).show();
                                v.getTask().getResult().
                                        getStorage().getDownloadUrl().addOnSuccessListener(uri -> URL_IMAGEM = uri.toString());
                            });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void validarDadosUsuario(View v){
        String nome, contato, endereco;
        nome        = etNome.getText().toString();
        contato      = etContato.getText().toString();
        endereco     = etEndereco.getText().toString();

        if (!nome.isEmpty()){
            if (!contato.isEmpty()){
                if (!endereco.isEmpty()){
                    Usuario u = new Usuario(nome,endereco,contato,URL_IMAGEM,ID_USUARIO_LOGADO);
                        u.salvar();
                        finish();
                }else     Toast.makeText(this, "Insira o endereço para entrega", Toast.LENGTH_SHORT).show();
            }else         Toast.makeText(this, "Insira um telefone para contato", Toast.LENGTH_SHORT).show();
        }else             Toast.makeText(this, "Insira seu nome", Toast.LENGTH_SHORT).show();
    }

    private void recuperarDadosUsuario(){
        Toast.makeText(this, "Carregando dados...", Toast.LENGTH_SHORT).show();
        DatabaseReference empresaRef = ConfiguracaoFirebase.getFirebase().child("usuarios")
                .child(ID_USUARIO_LOGADO);

        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Usuario u = dataSnapshot.getValue(Usuario.class);
                    etNome.setText(u.getNome());
                    etContato.setText(u.getContato());
                    etEndereco.setText(u.getEndereco());

                    if (u.getUrlImagem()!=null&&u.getUrlImagem()!=""){
                        URL_IMAGEM = u.getUrlImagem();
                        Picasso.get().load(URL_IMAGEM).error(R.drawable.ic_add_a_photo_black_24dp).into(civImagemUsuario);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
