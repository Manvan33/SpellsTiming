package com.manvan.spellstiming;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ApiQuery {

    private RequestQueue Queue;
    private JsonObjectRequest Req;
    private JsonArrayRequest ReqA;
    private static String API_KEY;

    public ApiQuery(Context context,String key) {
        Queue = Volley.newRequestQueue(context);
        API_KEY = key;
    }
    public ApiQuery(Context context) {
        Queue = Volley.newRequestQueue(context);
    }

    public void query(String url, Consumer<JSONObject> cons) {
        Queue.stop();
        Req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Volley", "got response");
                cons.accept(response);
                Log.i("Volley", "response "+response.toString());
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("Volley error",error.toString());
                cons.accept(null);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Riot-Token", API_KEY);
                return params;
            }
        };
        Queue.add(Req);
        Queue.start();
    }
    public void queryA(String url,Consumer<JSONArray> cons) {
        ReqA = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("Volley", "got response");
                cons.accept(response);
                Log.i("Volley", "response "+response.toString());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("Volley error",error.toString());
                        cons.accept(null);
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Riot-Token", API_KEY);
                return params;
            }
        };
        Queue.add(ReqA);
    }
}
