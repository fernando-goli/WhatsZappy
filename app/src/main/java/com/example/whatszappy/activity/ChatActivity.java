package com.example.whatszappy.activity;

import android.net.Uri;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatszappy.R;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Mensagem;
import com.example.whatszappy.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageChat;
    private TextView nameChat;
    private Usuario userDestinatario;
    private EditText editMsg;

    private String idUserReme;
    private String idUserDesti;
    private RecyclerView rvMsgs;


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
        editMsg = findViewById(R.id.editMsg);
        rvMsgs = findViewById(R.id.rvMsgs);

        //recuperar dados do usuario remetente
        idUserReme = UserFirebase.getUidFirebase();

        //recuperar dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            userDestinatario = ( Usuario ) bundle.getSerializable("chatContato");
            nameChat.setText( userDestinatario.getNome() );


            String photo = userDestinatario.getFoto();
            if( photo != null ){
                Uri url = Uri.parse( userDestinatario.getFoto() );
                Glide.with(ChatActivity.this)
                    .load(url)
                    .into(imageChat);

            } else {
                imageChat.setImageResource(R.drawable.padrao);
            }

            idUserDesti = userDestinatario.getIdUser();
        }

        //adapter

        //config recyclerview




    } // onCreate

    public void enviarMsg(View view){
        String txtMensagem = editMsg.getText().toString();

        if ( !txtMensagem.isEmpty()){

            Mensagem msg = new Mensagem();
            msg.setIdUser(idUserReme);
            msg.setMensagem(txtMensagem);

            salvarMsg(idUserReme, idUserDesti, msg);

        }else {
            Toast.makeText(ChatActivity.this,"Digite uma mensagem" , Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarMsg(String idReme, String idDesti, Mensagem mensagem){

        DatabaseReference dbRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference msgRef = dbRef.child("mensagens");

        msgRef.child(idReme)
            .child(idDesti)
            .push()
            .setValue(mensagem);

        editMsg.setText("");
    }


}
