package com.example.aj.helpinghand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

     EditText pass,email;
     ProgressBar bar;
     TextView signup;
     String url ="http://ec2-18-188-137-2.us-east-2.compute.amazonaws.com/user/login";

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         signup = findViewById(R.id.snp);
         email = findViewById(R.id.email);
         pass = findViewById(R.id.password);
         bar = findViewById(R.id.progressBar);
         bar.setIndeterminate(true);
         bar.setVisibility(View.INVISIBLE);

        Button login = findViewById(R.id._login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){

                    bar.setIndeterminate(true);
                    bar.setVisibility(View.VISIBLE);
                    JSONObject jsonObj = getjson(email.getText().toString(),pass.getText().toString());
                    JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                    bar.setVisibility(View.INVISIBLE);
                                    String result = null;
                                    try {
                                        result = response.getString("result");
                                    } catch (JSONException err) {
                                        Toast.makeText(LoginActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    if(result.equals("Success")){
                                        Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
                                        i.putExtra("email",email.getText().toString());
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            bar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                    VolleyHandler.getInstance(LoginActivity.this).getRequestQueue().add(jsonObjRequest);
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter Valid email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    public boolean validate(){
        return Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches() && !(pass.getText().toString().isEmpty());
    }

    public JSONObject getjson(String username, String password){
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", username);
        params.put("password",password);

        JSONObject jsonObj = new JSONObject(params);

        return jsonObj;
    }
}
