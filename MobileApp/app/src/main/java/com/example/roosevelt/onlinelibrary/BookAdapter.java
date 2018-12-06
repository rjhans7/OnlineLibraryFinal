package com.example.roosevelt.onlinelibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>
{
    public JSONArray elements;
    private Context context;


    public BookAdapter(JSONArray elements, Context context)
    {
        this.elements = elements;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView first_line, second_line;
        RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            first_line = itemView.findViewById(R.id.element_view_first_line);
            second_line = itemView.findViewById(R.id.element_view_second_line);
            container = itemView.findViewById(R.id.element_view_container);
        }
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_view,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        try {
            /*Esto lo hizo Diego. Usando el elements (jsonarray recibido como parámetro desde el BooksActivity)
            se extraen los valores que se quieren...
             */
            JSONObject element = elements.getJSONObject(position);
            String details = element.getString("autor")+" - "+element.getString("genero");
            final String title = element.getString("titulo");
            final String id = element.getString("ID");

            //... y se añaden a los holders dentro del xml
            holder.first_line.setText(title);
            holder.second_line.setText(details);

            //Con esto los holders pueden ser clickeados
            holder.container.setOnClickListener(new View.OnClickListener(){

                //Esta parte define lo que pasa cuando se hace click
                @Override //Con esto se redirige a la DisplayActivity
                public void onClick(View v) {Intent goToDisplay = new Intent(context,DisplayActivity.class);

                    /*Se añaden además otros dos parámetros: el id (para que sepa a cuál libro buscar)
                     y el título (para ponerlo como título
                      */
                    goToDisplay.putExtra("ID",id);
                    goToDisplay.putExtra("titulo", title);

                    //Se inicia la DisplayActivity
                    context.startActivity(goToDisplay);
                }
                //VER COMENTARIOS DEL DISPLAYACTIVITY ->
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return elements.length();
    }
}

