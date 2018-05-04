package com.example.aj.helpinghand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText email,pass,cpass;
    String url ="http://ec2-18-188-137-2.us-east-2.compute.amazonaws.com/user/signup";
    ProgressBar bar;


    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignupActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass_once);
        cpass = findViewById(R.id.conf_pass);

        bar = findViewById(R.id.progressBar3);
        bar.setIndeterminate(true);
        bar.setVisibility(View.INVISIBLE);

        Button signup = findViewById(R.id.signup_once);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate() ){
                    if(checkpass()){
                        bar.setVisibility(View.VISIBLE);
                        JSONObject jsonobj = getjson(email.getText().toString(),pass.getText().toString());
                        Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                        VolleyHandler.Post(url,SignupActivity.this, jsonobj,i,bar);
                    } else {
                        Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean validate(){
        return Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches() && !(pass.getText().toString().isEmpty());
    }

    public boolean checkpass(){
        return pass.getText().toString().equals(cpass.getText().toString());
    }

    public JSONObject getjson(String username, String password){
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", username);
        params.put("password",password);

        JSONObject jsonObj = new JSONObject(params);

        return jsonObj;
    }
}
