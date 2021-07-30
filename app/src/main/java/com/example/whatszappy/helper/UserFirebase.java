package com.example.whatszappy.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static String getUidFirebase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uidFirebase = user.getUid();

        return uidFirebase;
    }

    public static FirebaseUser getUserAtual() {
        FirebaseAuth user = ConfigFirebase.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static Boolean updatePhotoUser (Uri url){
        try{
            FirebaseUser user = getUserAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                .Builder()
                .setPhotoUri( url )
                .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar foto");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Boolean updateNameUser (String name){
        try{
            FirebaseUser user = getUserAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                .Builder()
                .setDisplayName( name )
                .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Usuario getDataUserLogged(){
        FirebaseUser fbUser = getUserAtual();

        Usuario user = new Usuario();
        user.setEmail(fbUser.getEmail());
        user.setNome(fbUser.getDisplayName());

        if(fbUser.getPhotoUrl() == null){
            user.setFoto("");
        }else {
            user.setFoto( fbUser.getPhotoUrl().toString());
        }

        return user;
    }
}
