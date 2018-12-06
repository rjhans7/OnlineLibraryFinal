package com.example.roosevelt.onlinelibrary;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity{

    //Declaramos una variable para saber si se dieron los permisos para leer contactos o no

    private static final  String TAG = "Register Activity";
    //URL del servidor
    private static final  String URL = "https://olibraryperu.herokuapp.com/mobile_register";
    //Para que muestre un mensaje de confirmación
    ProgressDialog progressDialog;

    // Variables de botones e inputs
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mUsernameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Crea los procesos de dialogo
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Crea todos los campos del formulario
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        //Botón de registro
        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        //Botón que redirecciona al login
        Button btnLinkLogin = (Button) findViewById(R.id.btn_link_login);
        // Link al login, si ya está registrado
        btnLinkLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }

    //Función que envía el formulario de registro
    private void submitForm() {

        // Resetea los errores presentados
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Asigna los valores de los inputs como strings
        final String username = mUsernameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Cheka la validez del password
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Cheka la validez del username

        if(TextUtils.isEmpty(username)){
           mUsernameView.setError(getString(R.string.error_field_required));
           focusView = mUsernameView;
           cancel = true;
        }else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Cheka la validez del email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel) {
            // Si existe un error
            // muestra el error y enfoca el error
            focusView.requestFocus();
        } else {
            //Si no hay errores en los campos, ejecuta el register
            registerUser(username, email, password);
        }
    }

    //Función que valida los usuarios, solo si son mayores de 4 caracteres
    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 4;
    }

    //Función que valida los email solo si contienen @
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    //Función que valida los password, solo si son mayores de 4 caracteres
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


//Función de registro de usuarios en el server
    private void registerUser(final String username, final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";

        progressDialog.setMessage("Adding you ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("users").getString("username");
                        Toast.makeText(getApplicationContext(), "Hi " + user + ", You are successfully Added!", Toast.LENGTH_SHORT).show();

                        // Launch login activity
                        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(login);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        // Conexion con el Register adapter para que el queue funcione
        RegisterAdapter.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog(){
    if(!progressDialog.isShowing())
        progressDialog.show();
    }
    private void hideDialog(){
    if(progressDialog.isShowing())
        progressDialog.dismiss();
    }
}