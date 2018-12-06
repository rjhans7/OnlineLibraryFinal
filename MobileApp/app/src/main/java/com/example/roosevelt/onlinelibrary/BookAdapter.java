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
    public String bookId;

    public BookAdapter(JSONArray elements, Context context, String bookId)
    {
        this.elements = elements;
        this.context = context;
        this.bookId = bookId;
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
            JSONObject element = elements.getJSONObject(position);
            String name = element.getString("autor")+" - "+element.getString("genero");
            final String username = element.getString("titulo");
            final String id = element.getString("ID");
            holder.first_line.setText(name);
            holder.second_line.setText(username);

            holder.container.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    /*Intent goToMessage = new Intent(context,MessageActivity.class);
                    goToMessage.putExtra("user_from_id",userFromId);
                    goToMessage.putExtra("user_to_id",id);
                    goToMessage.putExtra("username", username);
                    context.startActivity(goToMessage);*/
                }
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

