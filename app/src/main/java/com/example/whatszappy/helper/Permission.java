package com.example.whatszappy.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validarPermissoes(String[] permnissoes, Activity activity, int requestCode){
        //Verifica a versao do Celular pois só é necessario para o sistema maior que o 22
        //Marshmallow
        if(Build.VERSION.SDK_INT >= 23){ //Verifica a versao do Sistema
            List<String> listaPermissoes = new ArrayList<>();

            /*Percorre as permissoes passadas verificando uma a uma
             * se já tem a permissão liberada*/

            //========================================================================================
            // verifica permissões não existente e adiciona em listaPermissoes
            //========================================================================================

            for (String permissao : permnissoes){
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;
                if (!temPermissao){
                    listaPermissoes.add(permissao);
                }
            }
            // Caso a lista esteja vazia , não e necessário solicitar as permisões
            if (listaPermissoes.isEmpty()){
                return true;
            } else {
                //cria um array do tamanho da lista de permissões necessarias
                String[] novasPermissões = new String[listaPermissoes.size()];
                //convert a lista listaPermissoes em um array e adiciona nas Novas Permissoes
                listaPermissoes.toArray(novasPermissões);
                //Solicitar as permissões
                ActivityCompat.requestPermissions(activity, novasPermissões, requestCode);
            }
        }
        return true;
    }
}
