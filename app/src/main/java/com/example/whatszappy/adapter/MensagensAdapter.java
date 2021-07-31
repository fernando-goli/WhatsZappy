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
import com.example.whatszappy.helper.UserFirebase;
import com.example.whatszappy.model.Mensagem;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REME = 0;
    private static final int TIPO_DEST = 1;


    public MensagensAdapter (List<Mensagem> lista, Context c ) {
        this.mensagens = lista;
        this.context = c;
    }

    public MensagensAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        if( viewType == TIPO_REME){
            item = LayoutInflater.from( parent.getContext()).inflate(R.layout.adapter_mensagem_remetente,parent, false);

        } else if (viewType == TIPO_DEST){
            item = LayoutInflater.from( parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario,parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Mensagem mensagem = mensagens.get( position );
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImage();

        if( imagem != null ){
            Uri url = Uri.parse( imagem );
            Glide.with(context).load( url ).into( holder.imagem );

            holder.mensagemTxt.setVisibility( View.GONE );
        } else {
            holder.mensagemTxt.setText( msg );
            holder.imagem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem msg = mensagens.get( position );
        String idUsuario = UserFirebase.getUidFirebase();
        if (idUsuario.equals( msg.getIdUser() ) ){
            return TIPO_REME;
        }else{
            return TIPO_DEST;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mensagemTxt;
        ImageView imagem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mensagemTxt = itemView.findViewById(R.id.textMsgText);
            imagem = itemView.findViewById(R.id.imageMsgImage);
        }
    }


}
