package com.example.whatszappy.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatszappy.R;
import com.example.whatszappy.activity.ChatActivity;
import com.example.whatszappy.adapter.ConversasAdapter;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.helper.RecyclerItemClickListener;
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Conversa;
import com.example.whatszappy.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference databaseReference;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;

    public ConversasFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerConversas = view.findViewById(R.id.recycleListConversas);

        //adapter
        adapter = new ConversasAdapter(listaConversas, getActivity() );

        //recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity() );
        recyclerConversas.setLayoutManager( layoutManager );
        recyclerConversas.setHasFixedSize( true );
        recyclerConversas.setAdapter( adapter );
        String idUsuario = UserFirebase.getUidFirebase();

        //Evento clique
        recyclerConversas.addOnItemTouchListener(
            new RecyclerItemClickListener(
                getActivity(), recyclerConversas, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Conversa conversaSelect = listaConversas.get( position );
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato", conversaSelect.getUserExib() );
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


        databaseReference = ConfigFirebase.getFirebaseDatabase();
        conversasRef = databaseReference.child("conversas").child( idUsuario );

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void searchConversas(String texto){
        //Log.d("pesquisa", texto);

    }

    public void recuperarConversas(){

            childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                //recupera conversa
                Conversa conversa = snapshot.getValue( Conversa.class );
                listaConversas.add( conversa );
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
