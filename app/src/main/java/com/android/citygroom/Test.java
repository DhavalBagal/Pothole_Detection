package com.android.citygroom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Test extends AppCompatActivity {

    Button b;
    TextView t;
    RequestQueue queue;
    JsonObjectRequest req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(Test.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = "http://192.168.0.103:5000/test";

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("city", "london");
            postparams.put("timestamp", "1500134255");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        req = new JsonObjectRequest(url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            t.setText(response.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        b = findViewById(R.id.b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue.add(req);
                t = findViewById(R.id.txt);
            }
        });



    }
}

