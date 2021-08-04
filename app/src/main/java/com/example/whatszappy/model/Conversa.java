package com.example.whatszappy.model;

import com.example.whatszappy.config.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {

    private String idReme;
    private String idDest;
    private String lastMsg;
    private Usuario userExib;

    public Conversa() {
    }

    public void salvar(){

        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = databaseReference.child("conversas");

        conversaRef.child(this.getIdReme() )
            .child(this.getIdDest() )
            .setValue( this );
    }

    public String getIdReme() {
        return idReme;
    }

    public void setIdReme(String idReme) {
        this.idReme = idReme;
    }

    public String getIdDest() {
        return idDest;
    }

    public void setIdDest(String idDest) {
        this.idDest = idDest;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Usuario getUserExib() {
        return userExib;
    }

    public void setUserExib(Usuario userExib) {
        this.userExib = userExib;
    }
}
