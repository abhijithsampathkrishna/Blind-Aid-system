package com.example.aj.helpinghand;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by aj on 15/4/18.
 */

public class VolleyHandler {

    public static VolleyHandler mSingleton;
    private RequestQueue mQueue;
    private static Context mContext;

    private VolleyHandler(Context ctx) {
        mContext = ctx;
        mQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }
        return mQueue;
    }

    public synchronized static VolleyHandler getInstance(Context ctx) {
        if (mSingleton == null) {
            mSingleton = new VolleyHandler(ctx);
        }
        return mSingleton;
    }

    public static boolean Post(String url, final Context context, JSONObject jsonObj, final Intent intent, final ProgressBar pb) {

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                        if(pb!=null)pb.setVisibility(View.INVISIBLE);
                        if(intent != null)
                        context.startActivity(intent);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                if(pb!=null)pb.setVisibility(View.INVISIBLE);
                            }
                        });
        VolleyHandler.getInstance(context).getRequestQueue().add(jsonObjRequest);
        return true;
    }
}

