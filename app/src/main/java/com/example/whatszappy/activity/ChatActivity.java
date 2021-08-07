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
import com.example.whatszappy.helper.Base64Custom;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private EditText editMensagem;
    private ImageView imageCamera;
    private Usuario usuarioDestinatario;
    private Usuario usuarioRemetente;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private Grupo grupo;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private static final int SELECAO_CAMERA  = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracoes iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMsg);
        recyclerMensagens = findViewById(R.id.recyclerMsgs);
        imageCamera       = findViewById(R.id.imageCamera);

        //recupera dados do usuario remetente
        idUsuarioRemetente = UserFirebase.getIdentificadorUsuario();
        usuarioRemetente   = UserFirebase.getDadosUsuarioLogado();

        //Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();
        if ( bundle !=  null ){

            if( bundle.containsKey("chatGrupo") ){

                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();

                textViewNome.setText( grupo.getNome() );

                String foto = grupo.getFoto();
                if ( foto != null ){
                    Uri url = Uri.parse( foto );
                    Glide.with(ChatActivity.this)
                        .load(url)
                        .into( circleImageViewFoto );
                }else {
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }


            }else {
                /**********/
                usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
                textViewNome.setText( usuarioDestinatario.getNome() );

                String foto = usuarioDestinatario.getFoto();
                if ( foto != null ){
                    Uri url = Uri.parse(usuarioDestinatario.getFoto());
                    Glide.with(ChatActivity.this)
                        .load(url)
                        .into( circleImageViewFoto );
                }else {
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }

                //recuperar dados usuario destinatario
                idUsuarioDestinatario = Base64Custom.codificarBase64( usuarioDestinatario.getEmail() );
                /**********/
            }


        }

        //Configuração adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext() );

        //Configuração recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager( layoutManager );
        recyclerMensagens.setHasFixedSize( true );
        recyclerMensagens.setAdapter( adapter );

        database = ConfigFirebase.getFirebaseDatabase();
        storage = ConfigFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
            .child( idUsuarioRemetente )
            .child( idUsuarioDestinatario );


        //Evento de clique na camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_CAMERA );
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){

            Bitmap imagem = null;

            try {

                switch ( requestCode ){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                }

                if ( imagem != null ){

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    // Criar nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //Configurar referencia do firebase
                    StorageReference imagemRef = storage.child("imagens")
                        .child("fotos")
                        .child( idUsuarioRemetente )
                        .child( nomeImagem );

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this,
                                "Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            /*String dowloadUrl = taskSnapshot.getDownloadUrl().toString();

                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdUsuario( idUsuarioRemetente );
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem( dowloadUrl ); */

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();

                                    Mensagem mensagem = new Mensagem();
                                    mensagem.setIdUsuario( idUsuarioRemetente);
                                    mensagem.setMensagem(".jpeg");
                                    mensagem.setImagem( url.toString() );
                                    //Salvar mensagem remetente
                                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                                    //Salvar mensagem para o destinatario
                                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                                    Toast.makeText(ChatActivity.this,
                                        "Sucesso ao enviar imagem", Toast.LENGTH_SHORT).show();
                                }
                            });
                            
                        }
                    });

                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public void enviarMensagem(View view){

        String textoMensagem = editMensagem.getText().toString();

        if ( !textoMensagem.isEmpty() ){

            if ( usuarioDestinatario != null ){

                Mensagem mensagem = new Mensagem();
                mensagem.setIdUsuario( idUsuarioRemetente );
                mensagem.setMensagem( textoMensagem );

                //Salvar mensagem para o remetente
                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                //Salvar mensagem para o destinatario
                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                //Salvar conversa remetente
                salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario, mensagem, false);

                //Salvar conversa destinatario
                salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente, mensagem, false );

            }else {

                for ( Usuario membro: grupo.getMembros() ){

                    String idRemetenteGrupo = Base64Custom.codificarBase64( membro.getEmail() );
                    String idUsuarioLogadoGrupo = UserFirebase.getIdentificadorUsuario();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioLogadoGrupo );
                    mensagem.setMensagem( textoMensagem );
                    mensagem.setNome( usuarioRemetente.getNome() );

                    //salvar mensagem para o membro
                    salvarMensagem(idRemetenteGrupo, idUsuarioDestinatario, mensagem );

                    //Salvar conversa
                    salvarConversa( idRemetenteGrupo, idUsuarioDestinatario, usuarioDestinatario, mensagem, true);

                }

            }



        }else {
            Toast.makeText(ChatActivity.this,
                "Digite uma mensagem para enviar!",
                Toast.LENGTH_LONG).show();
        }

    }

    private void salvarConversa(String idRemetente, String idDestinatario, Usuario usuarioExibicao, Mensagem msg, boolean isGroup ){

        //Salvar conversa remetente
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente( idRemetente );
        conversaRemetente.setIdDestinatario( idDestinatario );
        conversaRemetente.setUltimaMensagem( msg.getMensagem() );

        if ( isGroup ){//conversa de grupo
            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo( grupo );
        }else {//Conversa normal
            conversaRemetente.setUsuarioExibicao( usuarioExibicao );
            conversaRemetente.setIsGroup("false");
        }

        conversaRemetente.salvar();

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){

        DatabaseReference database = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
            .child(idDestinatario)
            .push()
            .setValue(msg);

        //Limpar texto
        editMensagem.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener( childEventListenerMensagens );
    }

    private void recuperarMensagens(){

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue( Mensagem.class );
                mensagens.add( mensagem );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
