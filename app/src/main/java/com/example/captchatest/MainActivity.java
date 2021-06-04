package com.example.captchatest;

import android.os.Build;
import android.os.Bundle;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final List<String> LIST_SITE_KEYS =  Arrays.asList(
            "6Lc1dloaAAAAAEnAtPU5XaRfPDs7g7VjDZ44fcvJ", "6LdFdloaAAAAADM8ASaEOZV5lMm6hKebqrezjCOn",      // Antonio
            "6LdWdloaAAAAAF9n0VYMJrpkI2PLxG92Z9CDimBK",
            "6LdQ5FgaAAAAAHK_3lkh8FNih0sFCyP58t6PQ66L", "6LezOVkaAAAAAAkWOO2oxOpmvmV5YdUGiDTjjGUN",     // Inigo
            "6LckOlkaAAAAAPPBw01vWODm9R6NOl2ggNYqUPqo", //" 6LchOlkaAAAAACungz2ea4uNF48jk3Y1UJ5dVu6t",
            "6LfuUlQaAAAAAKX9cNoFnhO9QNkojJjXMNX_Tacu", "6Ld6UVQaAAAAAP0YkFL1yDZqNhO10n3L_JWa0fx9",      // Panos
            "6LdwUVQaAAAAALZFA-7SFbhMC78bFkffniJ5ASxh", "6LcI0VIaAAAAAFi_djTLDRcqUSV3eT8Ci7v8T3rc",
            "6LeR21IaAAAAAMvDUoe2DC1ftncOaG5tsBeK53Uq", "6Lc0SlQaAAAAAPv75oRspkZgO5D9xFOKFVlykJCL",     // Matteo
            "6LfScVkaAAAAANJceXW8IsJBaORWzZkn2f1Omv2t", "6LdKclkaAAAAABkag2FX9hcxggZMLRzbgV53Hujs");
    private final List<String> LIST_SECRET_KEY =  Arrays.asList(
            "6Lc1dloaAAAAAMwfV7fyEzd8W7YPYggaISQCJORB", "6LdFdloaAAAAACRhO3ra64KK-T2jPIVc9jTq2Iqy",
            "6LdWdloaAAAAAIQCqe2FifjD4DL3Td58VNESOEA4",
            "6LdQ5FgaAAAAAKQ9TVWzsYmobxTME7jvCntPH2vC", "6LezOVkaAAAAACt48qy8Jqq2MceAcEOWuUvv-wbW",
            "6LckOlkaAAAAAENg8bbaF_l9-1YhUrEPHCpZTSJQ", //"6LchOlkaAAAAADAttnU0RcgrssT3tYre__r2ziu6",
            "6LfuUlQaAAAAANJ4WzsXoqiwkgGI1tuwtmGhV7KK", "6Ld6UVQaAAAAAFCfvBBoEjiUziUd4g3FI4d0mqQa",
            "6LdwUVQaAAAAAFfDqaoYDYkld08txo--O_lskh6Y", "6LcI0VIaAAAAAEtfTqWZfp8KSI5-9Zcnt5c9sbJG",
            "6LeR21IaAAAAADLyZAtX7uK6psYp8-LbT3gGdZAD", "6Lc0SlQaAAAAANxjCz3tr8ZelW74zrh3v8dw5u6a",
            "6LfScVkaAAAAAPEfMVlNFmPLUCnq6kAGOMUyeTHN", "6LdKclkaAAAAANH9DC35LgIV9aSepS--gH2OzIfs");
    String SECRET_KEY = "";
    String SITE_KEY = "";
    String TAG = "com.example.captchatest";
    Button btnverifyCaptcha;
    RequestQueue queue;
    int numTests = 0;
    int numCaptchas = LIST_SITE_KEYS.size();
    long now = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(getApplicationContext());
        btnverifyCaptcha = findViewById(R.id.button);
        btnverifyCaptcha.setOnClickListener(this);
        final TextView textView = (TextView) findViewById(R.id.ipinfo);
        TextView textView2 = (TextView) findViewById(R.id.sitekey);
        textView2.setText("Site key: N/A");
        textView2 = (TextView) findViewById(R.id.duration);
        textView2.setText("Duration: N/A");
        /*textView2 = (TextView) findViewById(R.id.myip);
        textView2.setText("No-TOR IP should be: 98.109.116.129");*/
        String url = "https://ipinfo.io/ip";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,"IP:" + response);
                        textView.setText("Your IP: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        //long now = Instant.now().toEpochMilli();
        now = System.currentTimeMillis();
        SITE_KEY = LIST_SITE_KEYS.get(numTests);
        SECRET_KEY = LIST_SECRET_KEY.get(numTests);
        TextView textView = (TextView) findViewById(R.id.sitekey);
        textView.setText("Site-key: " + SITE_KEY);
        numTests += 1;
        numTests = numTests % numCaptchas; // rotate among site and secret keys
        Log.d(TAG, "Clicked-on-Captcha;SiteKey:" + SITE_KEY + ";Time:" + now);
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        Log.d(TAG, response.toString());
                        long current = System.currentTimeMillis();
                        long duration = current - now;
                        Log.d(TAG, "Captcha-Solved;Time:" + current + ";Duration:" + duration);
                        TextView textView2 = (TextView) findViewById(R.id.duration);
                        textView2.setText("Duration: " + duration + " ms");
                        if (!response.getTokenResult().isEmpty()) {
                            Log.d(TAG, "Non-empty-token;Site-Verification");
                            handleSiteVerify(response.getTokenResult());
                        } else {
                            Log.d(TAG, "Empty-token");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        long current = System.currentTimeMillis();
                        long duration = current - now;
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d(TAG, "Error:" + CommonStatusCodes.getStatusCodeString(apiException.getStatusCode())
                                    + ";Time:" + current + ";Duration:" + duration);
                        } else {
                            Log.d(TAG, "Unknown-Error:" + e.getMessage()
                                    + ";Time:" + current + ";Duration:" + duration );
                        }
                    }
                });

    }

    protected  void handleSiteVerify(final String responseToken){
        //it is google recaptcha siteverify server -- you can place your server url
        String url = "https://www.google.com/recaptcha/api/siteverify";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                Toast.makeText(getApplicationContext(),"SUCCESS", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getString("error-codes")),Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "JSON exception: " + ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error message: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }
}