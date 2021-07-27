package com.example.whatszappy.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static FirebaseAuth authentication;
    private static DatabaseReference database;
    private static StorageReference storage;

    //retorna a instancia do firebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
        if( database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //retorna a instancia do firebaseauth
    public static FirebaseAuth getFirebaseAuth() {

        if (authentication == null) {
            authentication = FirebaseAuth.getInstance();
        }
        return authentication;
    }

    public static StorageReference getFirebaseStorage(){
        if ( storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }


}


