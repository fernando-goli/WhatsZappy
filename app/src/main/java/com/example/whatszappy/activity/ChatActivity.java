package com.example.whatszappy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
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
import com.example.whatszappy.model.Conversa;
import com.example.whatszappy.model.Grupo;
import com.example.whatszappy.model.Mensagem;
import com.example.whatszappy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageChat, imageCamera;
    private TextView nameChat;
    private Usuario userDestinatario;
    private EditText editMsg;
    private static final int select_camera = 100;

    private String idUserReme;
    private String idUserDesti;

    private RecyclerView recyclerMsgs;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private DatabaseReference dbRef = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference msgRef;
    private ChildEventListener childEventListenerMensagens;
    private StorageReference storage;
    private Grupo grupo;

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
        imageCamera = findViewById(R.id.imageCamera);

        storage = ConfigFirebase.getFirebaseStorage();

        //recuperar dados do usuario remetente
        idUserReme = UserFirebase.getUidFirebase();

        //recuperar dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            if (bundle.containsKey("chatGrupo") ){

                grupo = ( Grupo ) bundle.getSerializable("chatGrupo");
                idUserDesti = grupo.getId();
                nameChat.setText( grupo.getNome() );

                String photo = grupo.getFoto();
                if( photo != null ){
                    Uri url = Uri.parse( photo );
                    Glide.with(ChatActivity.this)
                        .load(url)
                        .into(imageChat);

                } else {
                    imageChat.setImageResource(R.drawable.padrao);
                }


            } else {
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

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult(i, select_camera);
                }

            }
        });


    } // onCreate


    @Override //enviar imagem
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap image = null;
            try {
                switch (requestCode) {
                    case select_camera:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if ( image != null ) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();

                    //criar nome da image
                    String nameImg = UUID.randomUUID().toString();

                    //configurar referencia storage
                    final StorageReference imageRef = storage.child("imagens")
                        .child("fotos")
                        .child(idUserReme)
                        .child(nameImg);

                    //upload da imagem
                    UploadTask uploadTask = imageRef.putBytes(dataImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro camera", "Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();

                                    Mensagem msg = new Mensagem();
                                    msg.setIdUser( idUserReme);
                                    msg.setMensagem(".jpeg");
                                    msg.setImage( url.toString() );

                                    salvarMsg(idUserReme, idUserDesti, msg);
                                    salvarMsg(idUserDesti, idUserReme, msg);

                                    Toast.makeText(ChatActivity.this,
                                        "Sucesso ao fazer upload da foto!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    } //onActivityResult

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

            //salvar conversas
            salvarConversa(msg);

        }else {
            Toast.makeText(ChatActivity.this,"Digite uma mensagem" , Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarConversa( Mensagem msg){

        Conversa conversaReme = new Conversa();
        conversaReme.setIdReme( idUserReme );
        conversaReme.setIdDest( idUserDesti );
        conversaReme.setLastMsg( msg.getMensagem() );
        conversaReme.setUserExib( userDestinatario );

        conversaReme.salvar( );

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
