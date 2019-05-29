package br.com.ifood.cursoandroid.ifood.activity.empresa;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.model.Empresa;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {
    private static final int SELECAO_GALERIA = 200;
    private static String URL_IMAGEM;
    private static String ID_USUARIO_LOGADO;

    private Toolbar toolbar;
    private EditText etNome, etCategoria, etTaxaEntrega, etTempoEntrega;
    private CircleImageView civImagemEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);
        configurarLayout();
        configurarObjetos();
    }

    private void configurarObjetos() {
        //firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        ID_USUARIO_LOGADO = ConfiguracaoFirebase.getIdUsuario();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações de Empresa");
        setSupportActionBar(toolbar);

        civImagemEmpresa = findViewById(R.id.imgEmpresa);
        civImagemEmpresa.setOnClickListener(view -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )  ;

            if (i.resolveActivity(getPackageManager()) != null){
                startActivityForResult(i, SELECAO_GALERIA);
            }
        });
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
                    civImagemEmpresa.setImageBitmap(img);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] dadosImg = outputStream.toByteArray();

                    ConfiguracaoFirebase.getFirebaseStorage()
                            .child("imagens")
                            .child("empresas")
                            .child(ID_USUARIO_LOGADO+"jpeg")
                            .putBytes(dadosImg)
                                .addOnFailureListener(e -> Toast.makeText(this,
                                        "Erro ao fazer o upload da imagem, tente novamente",
                                        Toast.LENGTH_SHORT).show())
                                .addOnSuccessListener((v)-> {
                                    Toast.makeText(this,
                                            "Sucesso no upload", Toast.LENGTH_SHORT).show();
                                    URL_IMAGEM = v.getTask().getResult().
                                            getStorage().getDownloadUrl().toString();
                                });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void validarDadosEmpresa(View v){
        String nome, taxa, tempo, categoria;
        nome        = etNome.getText().toString();
        taxa        = etNome.getText().toString();
        tempo       = etNome.getText().toString();
        categoria   = etNome.getText().toString();

        if (!nome.isEmpty()){
            if (!categoria.isEmpty()){
                if (!taxa.isEmpty()){
                    if (!tempo.isEmpty()){
                        Empresa e = new Empresa(nome,ID_USUARIO_LOGADO, URL_IMAGEM, tempo,
                                categoria, Double.parseDouble(taxa));
                        e.salvar();
                        finish();
                    }else Toast.makeText(this, "Insira uma estimativa de tempo para entrega\nExemplo: 20-30", Toast.LENGTH_SHORT).show();
                }else     Toast.makeText(this, "Insira uma taxa de entrega\nExemplo: 20", Toast.LENGTH_SHORT).show();
            }else         Toast.makeText(this, "Insira uma categoria\nExemplo \"artesanal\"", Toast.LENGTH_SHORT).show();
        }else             Toast.makeText(this, "Insira o nome da empresa\nExemplo \"Tô brocado\" ", Toast.LENGTH_SHORT).show();
    }
}
