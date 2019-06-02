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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
        recuperarDadosEmpresa();
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

        etNome          = findViewById(R.id.tilNomeEmpresa);
        etTaxaEntrega   = findViewById(R.id.tilTaxaEntrega);
        etTempoEntrega  = findViewById(R.id.tilEntregaEmpresa);
        etCategoria     = findViewById(R.id.tilCategoriaEmpresa);
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
                    Toast.makeText(this,"Fazendo o upload da imagem...",
                            Toast.LENGTH_LONG).show();
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
                                    v.getTask().getResult().
                                            getStorage().getDownloadUrl().addOnSuccessListener(uri -> URL_IMAGEM = uri.toString());
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
        taxa        = etTaxaEntrega.getText().toString();
        tempo       = etTempoEntrega.getText().toString();
        categoria   = etCategoria.getText().toString();

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

    private void recuperarDadosEmpresa(){
        Toast.makeText(this, "Carregando dados...", Toast.LENGTH_SHORT).show();
        DatabaseReference empresaRef = ConfiguracaoFirebase.getFirebase().child("empresas")
                .child(ID_USUARIO_LOGADO);

        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Empresa e = dataSnapshot.getValue(Empresa.class);
                    etCategoria.setText(e.getCategoria());
                    etNome.setText(e.getNome());
                    etTempoEntrega.setText(e.getTempo());
                    etCategoria.setText(e.getCategoria());
                    etTaxaEntrega.setText(e.getTaxaEntrega().toString());

                    if (e.getUrlImagem()!=null&&e.getUrlImagem()!=""){
                        URL_IMAGEM = e.getUrlImagem();
                        Picasso.get().load(URL_IMAGEM).error(R.drawable.ic_add_a_photo_black_24dp).into(civImagemEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
