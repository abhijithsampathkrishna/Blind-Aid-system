package com.example.aj.helpinghand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ConfigActivity extends AppCompatActivity {

    final String geturl = "http://192.168.4.1/config/get";
    final String puturl = "http://192.168.4.1/config/set";
    String devId = null;
    ProgressBar pb;

    EditText ssid,password;
    TextView dev;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ConfigActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        ssid = findViewById(R.id.ssid);
        password = findViewById(R.id.passphrase);
        dev = findViewById(R.id.devid);
        Button update = findViewById(R.id.update);
        pb = findViewById(R.id.progressBar2);

        getInfo();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(devId != null ){
                    pb.setVisibility(View.VISIBLE);
                    pb.setIndeterminate(true);

                    if(!ssid.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        JSONObject jsonobj = getjson(ssid.getText().toString(), password.getText().toString());
                        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                                (Request.Method.POST, puturl, jsonobj, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(ConfigActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                        try {
                                            String success = response.getString("success");
                                        } catch (JSONException err) {
                                            Toast.makeText(ConfigActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        pb.setVisibility(View.INVISIBLE);


                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ConfigActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                                Toast.makeText(ConfigActivity.this, "Make sure you are connected to the device", Toast.LENGTH_SHORT).show();
                                                pb.setVisibility(View.INVISIBLE);
                                            }
                                        });
                        VolleyHandler.getInstance(ConfigActivity.this).getRequestQueue().add(jsonObjRequest);

                    } else {
                        Toast.makeText(ConfigActivity.this, "Please Provide SSID and password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ConfigActivity.this, "Cannot Communicate with device...Make sure you are connected", Toast.LENGTH_SHORT).show();
                    getInfo();
                }

            }
        });




    }

    public JSONObject getjson(String ssid, String password){
        Map<String, String> params = new HashMap<String, String>();
        params.put("ssid", ssid);
        params.put("password",password);
        params.put("devId",devId);

        JSONObject jsonObj = new JSONObject(params);

        return jsonObj;
    }

    public boolean getInfo(){
        pb.setVisibility(View.VISIBLE);
        pb.setIndeterminate(true);

        JSONObject jsonobj = new JSONObject();
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.POST, geturl,jsonobj ,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String s=null,p=null,id=null;
                        Toast.makeText(ConfigActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            s = response.getString("ssid");
                            p = response.getString("password");
                            id = response.getString("devID");
                            devId = id;
                        } catch (JSONException err){
                            Toast.makeText(ConfigActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
                        }
                        ssid.setText(s);
                        password.setText(p);
                        dev.setText("Device ID:" + id);
                        pb.setVisibility(View.INVISIBLE);


                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ConfigActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(ConfigActivity.this, "Make sure you are connected to the device", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.INVISIBLE);
                            }
                        });
        VolleyHandler.getInstance(ConfigActivity.this).getRequestQueue().add(jsonObjRequest);
    return  true;
    }
}
