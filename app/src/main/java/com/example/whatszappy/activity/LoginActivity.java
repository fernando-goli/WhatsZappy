package com.example.whatszappy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatszappy.R;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button buttonLogin;
    private FirebaseAuth loginAuth = ConfigFirebase.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginPassw);
        buttonLogin = findViewById(R.id.btnLogin);

    }

    public void validateUser(View view){
        //recuperar textos dos campos
        String textEmail = editEmail.getText().toString();
        String textPassw = editSenha.getText().toString();

        //validar campos preenchidos
        if (!textEmail.isEmpty()) {
            if (!textPassw.isEmpty()) {

                Usuario usuario = new Usuario();
                usuario.setEmail(textEmail);
                usuario.setSenha(textPassw);

                loginUser(usuario);

            } else {
                Toast.makeText(LoginActivity.this, "Preencha o senha", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginUser(Usuario usuario){

        loginAuth.signInWithEmailAndPassword(
            usuario.getEmail(),
            usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    openMain();

                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        excecao = "Digite um email valido!";
                    } catch ( FirebaseAuthInvalidCredentialsException e){
                        excecao = "Senha invalida!";
                    }catch (Exception e){
                        excecao = "Erro ao autenticar usuario!" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyUserLogged(){
        //recupera usuario atual e verifica se esta logado
        FirebaseUser user = loginAuth.getCurrentUser();
        if( user != null){
            openMain();
        }
    }

    public void register(View view){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void openMain(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyUserLogged();

    }
}
