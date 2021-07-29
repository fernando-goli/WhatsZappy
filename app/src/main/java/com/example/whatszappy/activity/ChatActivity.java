package com.example.whatszappy.activity;

import android.net.Uri;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatszappy.R;
import com.example.whatszappy.model.Usuario;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageChat;
    private TextView nameChat;
    private Usuario userDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //cria botao de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageChat = findViewById(R.id.circleImageChat);
        nameChat = findViewById(R.id.nameChat);

        //recuperar dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            userDest = ( Usuario ) bundle.getSerializable("chatContato");
            nameChat.setText( userDest.getNome() );

            String photo = userDest.getFoto();
            if( photo != null ){
                Uri url = Uri.parse( userDest.getFoto() );
                Glide.with(ChatActivity.this)
                    .load(url)
                    .into(imageChat);

            } else {
                imageChat.setImageResource(R.drawable.padrao);
            }
        }

    }








}
