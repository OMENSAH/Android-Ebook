package bawo.androidwithbackendapi;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressbar;
    private ListView listView;
    private RequestQueue queue;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        listView = (ListView) findViewById(R.id.listview);
//        progressbar = findViewById(R.id.progressbar);
        queue = Volley.newRequestQueue(this);

        final EditText email = findViewById(R.id.email);
        final EditText pass = findViewById(R.id.password);

        final User user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(pass.getText().toString());
        Button click = findViewById(R.id.button);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add( email.getText().toString(), pass.getText().toString());
            }
        });
        StrictMode.enableDefaults();

    }

    public  void add(String email, String pass){
        String url = "http://10.0.2.2:3000/user/signup";
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", pass);

        JsonObjectRequest jsObjRequest = new
                JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private void dataWithNoThreads(){
        showDialog();
        JsonObjectRequest post = new JsonObjectRequest(Request.Method.GET, "http://10.0.2.2/restapi/api/post/read.php", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayAdapter listViewAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.title);
                try {
                    JSONArray blogs = response.getJSONArray("data");
                    for (int i = 0; i < blogs.length(); i++) {
                        JSONObject blog = blogs.getJSONObject(i);
                        listViewAdapter.add(blog.get("title"));
                    }
                    listView.setAdapter(listViewAdapter);
                    hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MainActivity", "onResponse: "+ error.toString());
            }
        });
        queue.add(post);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
            }
        });
    }

    private void dataWithThreads(){
       thread = new Thread(new Runnable() {
           @Override
           public void run() {
               showDialog();
               JsonObjectRequest post = new JsonObjectRequest(Request.Method.GET, "http://10.0.2.2/restapi/api/post/read.php", new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       hideDialog();
                       ArrayAdapter listViewAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.title);
                       try {
                           JSONArray blogs = response.getJSONArray("data");
                           for (int i = 0; i < blogs.length(); i++) {
                               JSONObject blog = blogs.getJSONObject(i);
                               listViewAdapter.add(blog.get("title"));
                           }
                           listView.setAdapter(listViewAdapter);
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Log.d("MainActivity", "onResponse: "+ error.toString());
                   }
               });
               queue.add(post);
               queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                   @Override
                   public void onRequestFinished(Request<String> request) {
                       hideDialog();
                   }
               });
           }
       });
       thread.start();
    }

    private class AsyncTaskWorker extends AsyncTask<ProgressBar,Boolean,JSONObject>{
        private JSONObject output;
        @Override
        protected JSONObject doInBackground(ProgressBar... progressBars) {
            progressbar  = progressBars[0];
            publishProgress();
            JsonObjectRequest post = new JsonObjectRequest(Request.Method.GET, "http://10.0.2.2/restapi/api/post/read.php", new Response.Listener<JSONObject>() {
                
                @Override
                public void onResponse(JSONObject response) {
                    output = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MainActivity", "onResponse: "+ error.toString());
                }
            });
            queue.add(post);
            return output;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            ArrayAdapter listViewAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.title);
            try {
                JSONArray blogs = s.getJSONArray("data");
                for (int i = 0; i < blogs.length(); i++) {
                    JSONObject blog = blogs.getJSONObject(i);
                    listViewAdapter.add(blog.get("title"));
                }
                listView.setAdapter(listViewAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideDialog();
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            showDialog();
        }
    }
    private void showDialog(){
        progressbar.setVisibility(View.VISIBLE);
    }

    private void hideDialog(){
        if(progressbar.getVisibility() == View.VISIBLE){
            progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void showDialog(ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog(ProgressBar progressBar){
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
