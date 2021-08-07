package com.example.whatszappy.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatszappy.R;
import com.example.whatszappy.model.Conversa;
import com.example.whatszappy.model.Grupo;
import com.example.whatszappy.model.Mensagem;
import com.example.whatszappy.model.Usuario;

import java.util.List;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext() )
            .inflate( R.layout.adapter_contatos, parent, false);

        return new MyViewHolder( itemLista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Conversa conversa = conversas.get( position );
        holder.texto.setText( conversa.getLastMsg() );

        if( conversa.getIsGroup().equals("true") ) {

            Grupo grupo = conversa.getGrupo();
            holder.name.setText( grupo.getNome() );

            if (grupo.getFoto() != null) {
                Uri uri = Uri.parse(grupo.getFoto());
                Glide.with(context).load(uri).into(holder.photo);
            } else {
                holder.photo.setImageResource(R.drawable.padrao);
            }

        }else {

            Usuario usuario = conversa.getUserExib();
            holder.name.setText(usuario.getNome());

            if (usuario.getFoto() != null) {
                Uri uri = Uri.parse(usuario.getFoto());
                Glide.with(context).load(uri).into(holder.photo);
            } else {
                holder.photo.setImageResource(R.drawable.padrao);
            }

        }

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView photo;
        TextView name, texto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.circleImageContato);
            name = itemView.findViewById(R.id.textNameContato);
            texto = itemView.findViewById(R.id.textEmailContato);
        }
    }
}
