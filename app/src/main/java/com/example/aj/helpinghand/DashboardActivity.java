package com.example.aj.helpinghand;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    ArrayAdapter adapter;

    String email=null;
    String geturl ="http://ec2-18-188-137-2.us-east-2.compute.amazonaws.com/user/getdev";
    String puturl ="http://ec2-18-188-137-2.us-east-2.compute.amazonaws.com/user/adddev";
    String linkurl = "http://ec2-18-188-137-2.us-east-2.compute.amazonaws.com/link/add";
    String _geturl = "http://ec2-18-188-137-2.us-east-2.compute.amazonaws.com/location/getloc";
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final EditText id = findViewById(R.id.dev);
        final Button _add = findViewById(R.id._add);
        final ImageButton refresh = findViewById(R.id.imageButton);

        final ListView listv = findViewById(R.id.ls);
        Bundle extra = getIntent().getExtras();
        email = extra.getString("email");
        final ProgressBar bar = findViewById(R.id.progressBar4);
        bar.setIndeterminate(true);

//        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        ListHandler(bar,listv);

        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dev = adapter.getItem(i).toString();
                Toast.makeText(DashboardActivity.this, "Fetching Latest Location of "+dev , Toast.LENGTH_SHORT).show();
                bar.setVisibility(View.VISIBLE);
                JSONObject jsonObj = getjson(email,dev);


                JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                        (Request.Method.POST, _geturl, jsonObj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
//                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(DashboardActivity.this, "Fetching Location..", Toast.LENGTH_SHORT).show();
                                try {
                                    String lat = response.getString("lat");
                                    String lng = response.getString("lng");
                                    uri = "http://maps.google.com?q="+lat+","+lng;

                                } catch (JSONException err){
                                    Toast.makeText(DashboardActivity.this, "Device was not found", Toast.LENGTH_SHORT).show();
                                    bar.setVisibility(View.INVISIBLE);

                                }
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(uri));
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);
                                bar.setVisibility(View.INVISIBLE);

                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(DashboardActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        bar.setVisibility(View.INVISIBLE);
                                    }
                                });
                VolleyHandler.getInstance(DashboardActivity.this).getRequestQueue().add(jsonObjRequest);

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListHandler(bar,listv);
            }
        });

        _add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setVisibility(View.VISIBLE);
                String device = id.getText().toString();
                if(!(device.length()>0)){
                    Toast.makeText(DashboardActivity.this, "Please Enter Device ID", Toast.LENGTH_SHORT).show();
                }
                else {
                    VolleyHandler.Post(puturl,DashboardActivity.this,getjson(email,device),null,bar);
                    VolleyHandler.Post(linkurl,DashboardActivity.this,getjson(email,device),null,bar);


                }
            }
        });
    }

    public JSONObject getjson(String email,String dev){
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("dev",dev);
        params.put("id",dev);

        JSONObject jsonObj = new JSONObject(params);

        return jsonObj;

    }

    public void ListHandler(final ProgressBar bar,final ListView listv){
        if(! email.equals(null)){
            bar.setVisibility(View.VISIBLE);
            JSONObject jsonObj = getjson(email,null);
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                    (Request.Method.POST, geturl, jsonObj, new Response.Listener<JSONObject>() {

                        String[] list;
                        @Override
                        public void onResponse(JSONObject response) {
//                            Toast.makeText(DashboardActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            try {
                                JSONArray mobileArray = response.getJSONArray("devs");
//                                Toast.makeText(DashboardActivity.this, mobileArray.toString(), Toast.LENGTH_SHORT).show();
                                if(mobileArray!=null){
                                    list = new String[mobileArray.length()];
                                    for(int i=0;i<mobileArray.length();i++){
                                        list[i] = mobileArray.getString(i);

                                    }
                                } else {
                                    String[] list = {"No Devices found","Try Adding a device"};
                                }
                                adapter = new ArrayAdapter<String>(DashboardActivity.this,R.layout.list_view,R.id.label ,list);
                                bar.setVisibility(View.INVISIBLE);
                                listv.setAdapter(adapter);

                            } catch (JSONException err){
                                Toast.makeText(DashboardActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
                                bar.setVisibility(View.INVISIBLE);
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(DashboardActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    bar.setVisibility(View.INVISIBLE);
                                }
                            });
            VolleyHandler.getInstance(DashboardActivity.this).getRequestQueue().add(jsonObjRequest);
        }


    }
}
