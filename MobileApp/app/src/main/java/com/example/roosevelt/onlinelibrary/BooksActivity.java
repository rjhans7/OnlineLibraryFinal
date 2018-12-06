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

import java.util.HashMap;
import java.util.Map;

public class BooksActivity extends AppCompatActivity
{
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        mRecyclerView = findViewById(R.id.main_recycler_view);
        setTitle("Available Books");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getBooks();
    }

    public Activity getActivity(){return this;}

    public void getBooks()
    {
        String url = "https://olibraryperu.herokuapp.com/mobile_libros";
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<>();
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            mAdapter = new BookAdapter(data, getActivity());
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        error.printStackTrace();
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }
}
