package com.example.whatszappy.fragment;

import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatszappy.R;
import com.example.whatszappy.activity.ChatActivity;
import com.example.whatszappy.activity.GrupoActivity;
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

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuariosRefDb;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Configurações iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerListContatos);
        usuariosRefDb = ConfigFirebase.getFirebaseDatabase().child("usuarios");
        usuarioAtual = UserFirebase.getUsuarioAtual();

        //configurar adapter
        adapter = new ContatosAdapter(listaContatos, getActivity() );

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager );
        recyclerViewListaContatos.setHasFixedSize( true );
        recyclerViewListaContatos.setAdapter( adapter );

        //Configurar evento de clique no recyclerview
        recyclerViewListaContatos.addOnItemTouchListener(
            new RecyclerItemClickListener(
                getActivity(),
                recyclerViewListaContatos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario usuarioSelecionado = listaContatos.get( position );
                        boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                        if( cabecalho ){

                            Intent i = new Intent(getActivity(), GrupoActivity.class);
                            startActivity( i );

                        }else {
                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatContato", usuarioSelecionado );
                            startActivity( i );
                        }



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

        /*Define usuário com e-mail vazio
         * em caso de e-mail vazio o usuário será utilizado como
         * cabecalho, exibindo novo grupo */
        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo grupo");
        itemGrupo.setEmail("");

        listaContatos.add( itemGrupo );


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
        usuariosRefDb.removeEventListener( valueEventListenerContatos );
    }

    public void recuperarContatos(){

        valueEventListenerContatos = usuariosRefDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot dados: dataSnapshot.getChildren() ){

                    Usuario usuario = dados.getValue( Usuario.class );

                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if ( !emailUsuarioAtual.equals( usuario.getEmail() ) ){
                        listaContatos.add( usuario );
                    }


                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
