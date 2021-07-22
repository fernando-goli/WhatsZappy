package com.example.whatszappy.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {

    private static FirebaseAuth authentication;
    private static DatabaseReference database;
    private static FirebaseUser userFB;

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


}


