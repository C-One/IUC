package com.c1.gray.iuc;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private final int SPEECH_REQUEST_CODE = 123;
    TextView sayTextView;
    TextToSpeech myTTS;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button mic = (Button) findViewById(R.id.sendListenButton);
        mic.setOnClickListener(mSpeechToTextOnClickListener);

        sayTextView = (TextView) findViewById(R.id.sayTextView);
        editText = (EditText) findViewById(R.id.listenEditText);

        myTTS = new TextToSpeech(this, this);
        myTTS.setLanguage(Locale.KOREA);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Button.OnClickListener mSpeechToTextOnClickListener =
            new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        startActivityForResult(intent, SPEECH_REQUEST_CODE);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Your device is not supported!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));

                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = null;
                    try {
                        url = "http://10.113.237.176:5160/test?message=hi";
                        URLEncoder.encode(result.get(0), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                Log.i("Response:", response.toString());
                            sayTextView.setText("Response: " + response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error: ", error.toString());
                        }
                    }) {
                        @Override
                        public Map getHeaders() {
                            Map params = new HashMap<String, String>();
                            params.put("content-type", "application/json; charset=utf-8");
                            return params;

                        }

                    };
                    queue.add(jsonObjectRequest);

                }
                break;
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        String myText1 = "아이유, 안녕?";
        String utteranceId = this.hashCode() + "";
        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }
}
