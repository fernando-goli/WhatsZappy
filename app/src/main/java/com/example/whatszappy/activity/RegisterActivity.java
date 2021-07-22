package com.example.whatszappy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatszappy.R;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editName, editEmail, editPassw;
    private Button register;
    //recupera instancia do firebase para autenticar
    private FirebaseAuth fbAuth = ConfigFirebase.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editLoginEmail);
        editPassw = findViewById(R.id.editLoginPassw);
        register = findViewById(R.id.button);


    }//onCreate

    public void userDataValidation(View view) {

        //pega informação do editText
        String textName = editName.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textPassw = editPassw.getText().toString();

        //validar campos preenchidos
        if (!textName.isEmpty()) {
            if (!textEmail.isEmpty()) {
                if (!textPassw.isEmpty()) {

                    Usuario usuario = new Usuario();
                    usuario.setNome(textName);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textPassw);

                    registerUserFB(usuario);

                } else {
                    Toast.makeText(RegisterActivity.this, "Preencha o senha", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();
        }

    }

    public void registerUserFB(Usuario usuario){

        fbAuth.createUserWithEmailAndPassword( usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){


                    Toast.makeText(RegisterActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    String excecao = "";
                    //tratar exceção no cadastro
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite uma email valido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já foi cadastrada";
                    } catch (Exception e){
                        excecao = "Erro ao cadastrar usuario" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this, "Autenticação falhou", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fbAuth.getCurrentUser();
        if(currentUser != null ){
            currentUser.reload();
        }

    }

}


