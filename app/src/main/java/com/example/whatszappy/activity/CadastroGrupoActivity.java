package com.example.whatszappy.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.whatszappy.adapter.GrupoSelectAdapter;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.RecyclerItemClickListener;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Grupo;
import com.example.whatszappy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatszappy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CadastroGrupoActivity extends AppCompatActivity {

    private ArrayList<Usuario> listMemberSelect = new ArrayList<>();
    private GrupoSelectAdapter grupoSelectAdapter;
    private RecyclerView recyclerMembSelect;
    private TextView textTotalMembers;
    private EditText editNameGroup;
    private ImageView imageGroup;
    private static final int select_photo = 200;
    private Grupo grupo;
    private StorageReference storageReference;
    private FloatingActionButton fabNext;

    private DatabaseReference dbRef = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina o nome");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textTotalMembers = findViewById(R.id.textTotal);
        imageGroup = findViewById(R.id.imageGroup);
        editNameGroup = findViewById(R.id.editNameGroup);
        recyclerMembSelect = findViewById(R.id.recyclerMemberGroup);
        fabNext = findViewById(R.id.fabNextCadastro);
        grupo = new Grupo();

        storageReference = ConfigFirebase.getFirebaseStorage();

        //Recuperar lista de membros passada
        if ( getIntent().getExtras() != null){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listMemberSelect.addAll( membros );
            textTotalMembers.setText("Participantes: " + listMemberSelect.size());
        }

        //adicionar foto da galeria
        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult(i, select_photo);
                }
            }
        });

        //recyclerview para membros selecionados
        grupoSelectAdapter = new GrupoSelectAdapter(listMemberSelect, getApplicationContext());

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(
            getApplicationContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        );
        recyclerMembSelect.setLayoutManager(layoutManager1);
        recyclerMembSelect.setHasFixedSize( true );
        recyclerMembSelect.setAdapter( grupoSelectAdapter );

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeGroup = editNameGroup.getText().toString();
                //TODO: Passar id do criador do grupo junto.
                listMemberSelect.add( UserFirebase.getDataUserLogged());
                grupo.setMembros( listMemberSelect );

                grupo.setNome( nomeGroup );
                grupo.salvar();

            }
        });


    }//onCreate

    //metodo para utilizar o galeria do celular
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap image = null;

            try {

                Uri localImagemSelecionada = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada );

                if ( image != null ){
                    imageGroup.setImageBitmap( image );

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    final StorageReference imageRef = storageReference
                        .child("imagens")
                        .child("grupos")
                        .child( grupo.getId() + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroGrupoActivity.this,
                                "Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CadastroGrupoActivity.this,
                                "Sucesso ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    grupo.setFoto( url.toString() );

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


}
