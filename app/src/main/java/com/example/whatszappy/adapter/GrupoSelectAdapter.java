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

public class GrupoSelectAdapter extends RecyclerView.Adapter<GrupoSelectAdapter.MyViewHolder> {

    private List<Usuario> contatosSelect;
    private Context context;

    public GrupoSelectAdapter(List<Usuario> listContatos, Context c) {
        this.contatosSelect = listContatos;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from( parent.getContext() )
            .inflate(R.layout.adapter_grupo_select, parent, false);

        return new GrupoSelectAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(GrupoSelectAdapter.MyViewHolder holder, int position) {
        Usuario user = contatosSelect.get( position );

        holder.name.setText( user.getNome() );

        if (user.getFoto() != null ){
            Uri uri = Uri.parse( user.getFoto());
            Glide.with( context ).load(uri).into(holder.photo);
        } else {
            holder.photo.setImageResource( R.drawable.padrao );
        }

    }

    @Override
    public int getItemCount() {
        return contatosSelect.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView photo;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.circleImageGroupSelect);
            name = itemView.findViewById(R.id.nameGroupSelect);
        }
    }
}
