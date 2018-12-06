package com.example.roosevelt.onlinelibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//PARTE 3
//VER ANTES LOS COMENTARIOS DEL DISPLAYACTIVITY

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.ViewHolder> {

    public JSONArray elements;
    private Context mContext;

    public DisplayAdapter(JSONArray elements, Context mContext) {
        //El JSONArray que le pasamos del DisplayActivity (ahí llamado data) se convierte en una variable private llamada elements
        this.elements = elements;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcionLine;
        TextView autorLine;
        TextView generoLine;
        RelativeLayout container;
        //Button downloadButton;

        public ViewHolder(View itemView) {
            super(itemView);
            descripcionLine = itemView.findViewById(R.id.element_view_descripcion);
            autorLine = itemView.findViewById(R.id.element_view_autor);
            generoLine = itemView.findViewById(R.id.element_view_genero);
            container = itemView.findViewById(R.id.element_view_container);
            //downloadButton = itemView.findViewById(R.id.download_btn);
        }
    }

    @NonNull
    @Override
    public DisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(
                R.layout.activity_display_adapter, parent, false
        );
        return new DisplayAdapter.ViewHolder(view);
    }

    //AQUÍ EMPIEZAN LOS PROBLEMAS
    @Override
    public void onBindViewHolder(@NonNull DisplayAdapter.ViewHolder holder, int position) {
        try{
            //Se obtienen los datos del libro, sacándolos de la variable elements (el data que obtuvimos del DisplayActivity)
            JSONObject element = elements.getJSONObject(position);

            //Se extrae el elemento en específico que queremos (en este caso, la descripción)
            String descripcion = "Descripción: " + element.getString("descripcion");
            String autor = "Autor: " + element.getString("autor");
            String genero = "Género: " + element.getString("genero");
            //Hacemos que un holder muestre a info que hemos sacado (la descripcion)
            holder.autorLine.setText(autor);
            holder.generoLine.setText(genero);
            holder.descripcionLine.setText(descripcion);

            /*El error que salta es:
            java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference
            Es decir, se está tratando de poner en el holder un elemento null
            Al parecer, no se está recibiendo bien el jsonArray o el jsonObject en alguna de las instancias,
             por lo que al buscar "descripcion" este retorna un null (no hay)
            */


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //Aparentemente este es el que define los valores de position: se itera position de acuerdo a la cantidad de elementos que existen en elements. Aquí solo hay 1
    @Override
    public int getItemCount() {
        return elements.length();
    }


    //FIN DE COMENTARIOS

}
