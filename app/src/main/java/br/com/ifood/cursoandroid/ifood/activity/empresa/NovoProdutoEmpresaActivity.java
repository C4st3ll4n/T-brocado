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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.com.ifood.cursoandroid.ifood.R;
import br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase;
import br.com.ifood.cursoandroid.ifood.helper.StringHelper;
import br.com.ifood.cursoandroid.ifood.model.Empresa;
import br.com.ifood.cursoandroid.ifood.model.Produto;
import de.hdodenhof.circleimageview.CircleImageView;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {
    private static final int SELECAO_GALERIA = 200;

    private static String URL_IMAGEM;
    private static String ID_USUARIO_LOGADO;

    private Toolbar toolbar;
    private EditText etNomeProduto, etPrecoPrduto, etDescricaoProduto;
    private CircleImageView civImagemProdutp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);
        configurarLayout();
        configurarObjetos();
    }

    private void configurarLayout() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);

        etNomeProduto = findViewById(R.id.etNomeProdutoEmpresa);
        etPrecoPrduto = findViewById(R.id.etPrecoProdutoEmpresa);
        etDescricaoProduto = findViewById(R.id.etDescricaoProdutoEmpresa);
        civImagemProdutp = findViewById(R.id.civImagemProdutoEmpresa);
        civImagemProdutp.setOnClickListener( (v) -> {
            Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,SELECAO_GALERIA);
        });
    }

    private void configurarObjetos() {
        //firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        ID_USUARIO_LOGADO = ConfiguracaoFirebase.getIdUsuario();
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
                    civImagemProdutp.setImageBitmap(img);
                    Toast.makeText(this,"Fazendo o upload da imagem...",
                            Toast.LENGTH_LONG).show();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] dadosImg = outputStream.toByteArray();

                    String nome = StringHelper.removerCaracteresEspeciais(etNomeProduto.getText().toString());

                    ConfiguracaoFirebase.getFirebaseStorage()
                            .child("imagens")
                            .child("produtos")
                            .child(ID_USUARIO_LOGADO+nome+"jpeg")
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

    public void validarDadosProduto(View v){
        String nome, preco, descricao;
        nome        = etNomeProduto.getText().toString();
        preco       = etPrecoPrduto.getText().toString();
        descricao   = etDescricaoProduto.getText().toString();

        if (!nome.isEmpty()){
            if (!preco.isEmpty()){
                if (!descricao.isEmpty()){
                    String idzinho = StringHelper.removerCaracteresEspeciais(etNomeProduto.getText().toString())+ID_USUARIO_LOGADO;
                    Produto p = new Produto(ID_USUARIO_LOGADO,nome,descricao,URL_IMAGEM,Double.parseDouble(preco));
                        p.salvar();
                        finish();
                    }else Toast.makeText(this, "Insira uma descrição\nExemplo: Hamburger com carne e pão artesanal", Toast.LENGTH_SHORT).show();
                }else     Toast.makeText(this, "Insira um preço\nExemplo: 18", Toast.LENGTH_SHORT).show();
            }else         Toast.makeText(this, "Insira um nome para o produto\nExemplo \"Hamburger artesanal\"", Toast.LENGTH_SHORT).show();
    }

    private void recuperarDadosProduto(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebase().child("produtos");

        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Produto p = dataSnapshot.getValue(Produto.class);
                  /*  etCategoria.setText(e.getCategoria());
                    etNome.setText(e.getNome());
                    etTempoEntrega.setText(e.getTempo());
                    etCategoria.setText(e.getCategoria());
                    etTaxaEntrega.setText(e.getTaxaEntrega().toString());

                    if (e.getUrlImagem()!=null&&e.getUrlImagem()!=""){
                        URL_IMAGEM = e.getUrlImagem();
                        Picasso.get().load(URL_IMAGEM).into(civImagemEmpresa);
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
