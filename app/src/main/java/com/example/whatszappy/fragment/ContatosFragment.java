package com.example.whatszappy.fragment;

import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatszappy.R;
import com.example.whatszappy.activity.ChatActivity;
import com.example.whatszappy.adapter.ContatosAdapter;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.RecyclerItemClickListener;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView rvListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference userRefDb;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser userAtual;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //config inicial
        rvListaContatos = view.findViewById(R.id.recyclerListContatos);
        userRefDb = ConfigFirebase.getFirebaseDatabase().child("usuarios");
        userAtual = UserFirebase.getUserAtual();

        //adapter
        adapter = new ContatosAdapter( listaContatos, getActivity() );

        //recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity() );
        rvListaContatos.setLayoutManager( layoutManager );
        rvListaContatos.setHasFixedSize(true);
        rvListaContatos.setAdapter( adapter );

        //configura evento de clique no recyclerview
        rvListaContatos.addOnItemTouchListener(
            new RecyclerItemClickListener(
                getActivity(), rvListaContatos, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    startActivity( i );
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


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRefDb.removeEventListener( valueEventListenerContatos );
    }

    public void recuperarContatos(){

        valueEventListenerContatos = userRefDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for ( DataSnapshot dados: snapshot.getChildren() ){
                    Usuario user = dados.getValue(Usuario.class);

                    String emailUserAtual = userAtual.getEmail();
                    if ( !emailUserAtual.equals(user.getEmail() ) ){
                        listaContatos.add( user );
                    }


                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
