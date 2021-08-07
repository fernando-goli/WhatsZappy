package com.example.whatszappy.model;


import com.example.whatszappy.config.ConfigFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable {

    private String id;
    private String nome;
    private String foto;
    private List<Usuario> membros;

    public Grupo() {
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference grupoRef = databaseReference.child("grupos");

        String idGrupoFirebase = grupoRef.push().getKey();
        setId( idGrupoFirebase );

    }

    public void salvar(){
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference grupoRef = databaseReference.child("grupos");

        grupoRef.child( getId() ).setValue( this );

        for( Usuario membro: getMembros()){

            //FirebaseUser uidUserFb = FirebaseAuth.getInstance().getCurrentUser();
            //String uidFirebase = uidUserFb.getUid();

            String idRemet = membro.getIdUser();
            String idDesti = getId();

            Conversa conversa = new Conversa();
            conversa.setIdReme( idRemet );
            conversa.setIdDest( idDesti );
            conversa.setLastMsg("");
            conversa.setIsGroup("true");
            conversa.setGrupo( this );

            conversa.salvar();
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
