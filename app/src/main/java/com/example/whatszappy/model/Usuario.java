package com.example.whatszappy.model;

import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String nome, email, senha, idUser, foto;

    public Usuario() {
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    //TODO: Por enquanto manter comentado o exclude.
    //@Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void salvar(){
        DatabaseReference firebaseDb = ConfigFirebase.getFirebaseDatabase();
        firebaseDb.child("usuarios")
            .child(this.getIdUser())
            .setValue( this );
    }

    public void update() {
        String uidUser = UserFirebase.getUidFirebase();
        DatabaseReference database = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference userRef = database
            .child("usuarios")
            .child(uidUser);

        Map<String, Object> valueUser = convertMap();

        userRef.updateChildren(valueUser);
    }

    @Exclude
    public Map <String, Object> convertMap () {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("nome", getNome());
        userMap.put("foto", getFoto());

        return userMap;
    }
}
