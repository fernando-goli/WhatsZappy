package com.example.whatszappy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatszappy.R;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ConfigActivity extends AppCompatActivity {

    private EditText nameUser;
    private ImageButton buttonCamera, buttonPhoto;
    private String[] permissionMobile = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };
    private static final int select_camera = 100;
    private static final int select_photo = 200;
    private ImageView imageUser;
    private String uidFirebase;
    private Usuario userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        //cria botao de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameUser = findViewById(R.id.editName);
        buttonCamera = findViewById(R.id.btnCamera);
        buttonPhoto = findViewById(R.id.btnPhoto);
        imageUser = findViewById(R.id.circleImageContato);


        //verifica usuario logado
        userLogged = UserFirebase.getDataUserLogged();

        //config reference
        FirebaseUser uidUserFb = FirebaseAuth.getInstance().getCurrentUser();
        uidFirebase = uidUserFb.getUid();


        //Recuperar user atual
        FirebaseUser user = UserFirebase.getUserAtual();
        Uri url = user.getPhotoUrl();

        if(url != null){
            Glide.with(ConfigActivity.this)
                .load(url)
                .into(imageUser);
        }else {
            imageUser.setImageResource(R.drawable.padrao);
        }
        nameUser.setText( user.getDisplayName());

        //button
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult(i, select_camera);
                }

            }
        });

        //button
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult(i, select_photo);
                }

            }
        });


    } //onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap image = null;

            try {
                switch ( requestCode ){
                    case select_camera:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case select_photo:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }
                if ( image != null ){

                    imageUser.setImageBitmap( image );

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();


                    //salvar imagem no firebase
                    final StorageReference imageRef = ConfigFirebase.getFirebaseStorage()
                        .child("imagens")
                        .child("perfil")
                        .child(uidFirebase)
                        .child("perfil.jpeg");

                    UploadTask uploadTask = imageRef.putBytes( dataImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigActivity.this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigActivity.this, "Sucesso ao fazer upload", Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    updatePhoto( url );

                                }
                            });

                        }
                    });

                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permResult : grantResults){
            if( permResult == PackageManager.PERMISSION_DENIED){
                alertValidatePerm();
            }
        }

    }

    private void alertValidatePerm(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes negadas");
        builder.setMessage("Para utilizar o app é necessario aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void saveName(View view){
        String name = nameUser.getText().toString();
        Boolean userN = UserFirebase.updateNameUser(name);
        if (userN) {

            userLogged.setNome( name );
            userLogged.update();

            Toast.makeText(ConfigActivity.this, "Nome alterado", Toast.LENGTH_SHORT).show();

        }
    }

    public void updatePhoto(Uri url){
        Boolean returno = UserFirebase.updatePhotoUser(url);
        if (returno){
            userLogged.setFoto( url.toString() );
            userLogged.update();
            Toast.makeText(ConfigActivity.this, "Foto atualizada", Toast.LENGTH_SHORT).show();

        }
    }

}
