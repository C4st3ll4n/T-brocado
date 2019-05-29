package br.com.ifood.cursoandroid.ifood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static br.com.ifood.cursoandroid.ifood.helper.ConfiguracaoFirebase.getFirebaseAutenticacao;

public class UsuarioFirebase {

    public static String getIdUsuario(){

        FirebaseAuth autenticacao = getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();

    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarTipoUsuario(String tipo){
        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(userProfileChangeRequest);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
