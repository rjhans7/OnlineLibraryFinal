package com.example.roosevelt.onlinelibrary;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //Aquí se obtiene el título del putExtra del BookAdapter y se pone como título
        String titulo = getIntent().getExtras().get("titulo").toString();
        setTitle(titulo);
        mRecyclerView = findViewById(R.id.main_recycler_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();
    }

    public void getData() {
        //Aquí se obtiene el ID del libro que queremos sacándolo del putExtra del BookAdapter
        final String ID = getIntent().getExtras().get("ID").toString();

        /*Aquí se está generando la url para pedir el json con los datos en específico de este libro
        Para eso empleamos el ID que sacamos en la línea anterior
         */
        String url = "https://olibraryperu.herokuapp.com/mobile_libros/<ID>";
        url = url.replace("<ID>", ID);

        //Se usa la url para hacer una request al servidor: se pide el json del libro con la ID
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // Aquí está recibiendo la respuesta a la request (debería estar en formato jsonarray): los datos del libro
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { //<- ¿Por qué indican JSONObjecto y no JSONArray? Ni idea. Así estaba en el código del profe
                        try {
                            /*Del jsonObject obtenido se extrae el array que se encuentra bajo el
                             label data (ese formato se especifica en el server
                              */
                            JSONArray data = response.getJSONArray("data");

                            //Se conecta con el adaptador DisplayAdapter, pasando el array extraído como argumento
                            mAdapter = new DisplayAdapter(data, getActivity());
                            mRecyclerView.setAdapter(mAdapter);

                            //PASAR A DISPLAYADAPTER ->
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        queue.add(request);
    }

}
