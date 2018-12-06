package com.example.roosevelt.onlinelibrary;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    //Declaramos los inputs y botones a utilizar
    private EditText txtUsername, txtPassword;
    private Button btnLogin, btnLinkRegister;

    //Asignamos el link del server, debe estar en http, porque en https no funciona
    private static final  String URL = "https://olibraryperu.herokuapp.com/mobile_login";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("OnlineLibrary - Peru");

        //Asignamos el valor de los inputs
        txtUsername = (EditText) findViewById(R.id.txtusername);
        txtPassword = (EditText) findViewById(R.id.txtpassword);

        //Asignamos el valor de los botones

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLinkRegister = (Button)findViewById(R.id.btn_link_register);

        //Asignamos las funciones a los botones

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtnLogin();
            }
        });

        btnLinkRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtnRegister();
            }
        });

    }

    public void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public Activity getActivity()
    {
        return this;
    }

    public void onClickBtnLogin()
    {

        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap();
        params.put("username", txtUsername.getText().toString());
        params.put("password", txtPassword.getText().toString());

        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (
                        Request.Method.POST,
                        URL,
                        parameters,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean ok = response.getBoolean("response");
                                    if (ok) {
                                        Intent intent = new Intent(getActivity(), BooksActivity.class);
                                        startActivity(intent);
                                    } else {
                                        showMessage("Wrong Username or Password");
                                    }
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
                                showMessage(error.getMessage());
                            }
                        });
        queue.add(jsonObjectRequest);
    }

    public void onClickBtnRegister(){
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
    }
}

