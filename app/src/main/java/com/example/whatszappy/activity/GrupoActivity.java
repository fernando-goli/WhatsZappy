package com.example.whatszappy.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.whatszappy.adapter.ContatosAdapter;
import com.example.whatszappy.adapter.GrupoSelectAdapter;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.RecyclerItemClickListener;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.example.whatszappy.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class GrupoActivity extends AppCompatActivity {

    private ArrayList<Usuario> listMember = new ArrayList<>();
    private ArrayList<Usuario> listMemberSelect = new ArrayList<>();
    private RecyclerView recyclerMembSelect, recyclerMemb;
    private ContatosAdapter contatosAdapter;
    private GrupoSelectAdapter grupoSelectAdapter;
    private ValueEventListener valueEventListenerMember;
    private DatabaseReference userRefDb;
    private FirebaseUser userAtual;
    private Toolbar toolbar;
    private FloatingActionButton fabNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //config iniciais
        userAtual = UserFirebase.getUserAtual();
        userRefDb = ConfigFirebase.getFirebaseDatabase().child("usuarios");
        recyclerMemb = findViewById(R.id.recyclerMember);
        recyclerMembSelect = findViewById(R.id.recyclerMemberSelect);
        fabNext = findViewById(R.id.fabNextCadastro);


        //Configura recyclerview para os contatos
        contatosAdapter = new ContatosAdapter(listMember, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerMemb.setLayoutManager(layoutManager);
        recyclerMemb.setHasFixedSize( true );
        recyclerMemb.setAdapter( contatosAdapter );

        recyclerMemb.addOnItemTouchListener(
            new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMemb,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario userSelect = listMember.get( position );

                        //remove user selecionado na lista geral
                        listMember.remove( userSelect );
                        contatosAdapter.notifyDataSetChanged();

                        //adiciona user na lista do grupo
                        listMemberSelect.add( userSelect );
                        grupoSelectAdapter.notifyDataSetChanged();

                        atualizarMemberToolbar();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        )); //recyclerMemb

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


        recyclerMembSelect.addOnItemTouchListener(
            new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMembSelect,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //remove da lista de membros selecionado
                        Usuario userSelect = listMemberSelect.get( position );
                        listMemberSelect.remove( userSelect );
                        grupoSelectAdapter.notifyDataSetChanged();

                        //add a lista de membros
                        listMember.add( userSelect );
                        contatosAdapter.notifyDataSetChanged();

                        atualizarMemberToolbar();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    }
                }
            )
        );

        //fab
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GrupoActivity.this, CadastroGrupoActivity.class);
                i.putExtra( "membros", listMemberSelect);
                startActivity( i );
            }
        });


    }//onCreate

    public void recuperarContatos(){
        valueEventListenerMember = userRefDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot dados: snapshot.getChildren() ){
                    Usuario user = dados.getValue(Usuario.class);

                    String emailUserAtual = userAtual.getEmail();
                    if ( !emailUserAtual.equals(user.getEmail() ) ){
                        listMember.add( user );
                    }
                }
                contatosAdapter.notifyDataSetChanged();
                atualizarMemberToolbar();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRefDb.removeEventListener( valueEventListenerMember );
    }

    public void atualizarMemberToolbar(){
        int totalSlect = listMemberSelect.size();
        int total = listMember.size() + totalSlect;
        toolbar.setSubtitle(totalSlect + " de " + total + " selecionados");
    }

}
