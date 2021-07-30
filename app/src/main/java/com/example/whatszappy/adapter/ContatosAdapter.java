package com.example.whatszappy.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatszappy.R;
import com.example.whatszappy.model.Usuario;

import java.util.List;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHolder> {

    private List<Usuario> contatos;
    private Context context;

    public ContatosAdapter(List<Usuario> listContatos, Context c) {
        this.contatos = listContatos;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from( parent.getContext() )
            .inflate(R.layout.adapter_contatos, parent, false);


        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario user = contatos.get( position );

        holder.name.setText( user.getNome() );
        holder.email.setText( user.getEmail() );

        if (user.getFoto() != null ){
            Uri uri = Uri.parse( user.getFoto());
            Glide.with( context ).load(uri).into(holder.photo);
        } else {
            holder.photo.setImageResource( R.drawable.padrao );
        }

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView photo;
        TextView name, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.circleImageContato);
            name = itemView.findViewById(R.id.textNameContato);
            email = itemView.findViewById(R.id.textEmailContato);


        }
    }

}
