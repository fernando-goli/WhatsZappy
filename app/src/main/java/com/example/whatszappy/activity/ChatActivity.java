package com.example.whatszappy.activity;

import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatszappy.R;
import com.example.whatszappy.adapter.MensagensAdapter;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Mensagem;
import com.example.whatszappy.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageChat;
    private TextView nameChat;
    private Usuario userDestinatario;
    private EditText editMsg;

    private String idUserReme;
    private String idUserDesti;

    private RecyclerView recyclerMsgs;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private DatabaseReference dbRef = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference msgRef;
    private ChildEventListener childEventListenerMensagens;

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
        nameChat = findViewById(R.id.nameChatContato);
        editMsg = findViewById(R.id.editMsg);
        recyclerMsgs = findViewById(R.id.recyclerMsgs);

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
            //recupera dados user destinatario
            idUserDesti = userDestinatario.getIdUser();
        }

        //config adapter
        adapter = new MensagensAdapter( mensagens, getApplicationContext() );

        //config recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerMsgs.setLayoutManager( layoutManager );
        recyclerMsgs.setHasFixedSize( true );
        recyclerMsgs.setAdapter( adapter );

        msgRef = dbRef.child("mensagens")
            .child( idUserReme )
            .child( idUserDesti );



    } // onCreate

    public void enviarMsg(View view){
        String txtMensagem = editMsg.getText().toString();

        if ( !txtMensagem.isEmpty()){

            Mensagem msg = new Mensagem();
            msg.setIdUser(idUserReme);
            msg.setMensagem(txtMensagem);

            //salvar mensagem para o remetente
            salvarMsg(idUserReme, idUserDesti, msg);

            //salvar mensagem para o destinatario
            salvarMsg(idUserDesti, idUserReme, msg);

        }else {
            Toast.makeText(ChatActivity.this,"Digite uma mensagem" , Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarMsg(String idReme, String idDesti, Mensagem msg){

        msgRef = dbRef.child("mensagens");

        msgRef.child(idReme)
            .child(idDesti)
            .push()
            .setValue(msg);
        //limpa texto
        editMsg.setText("");
    }

   @Override
    protected void onStart() {
        super.onStart();
        recuperarMsg();
    }

   @Override
    protected void onStop() {
        super.onStop();
        msgRef.removeEventListener( childEventListenerMensagens );
    }

    private void recuperarMsg(){

        childEventListenerMensagens = msgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue( Mensagem.class);
                mensagens.add( mensagem );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}
