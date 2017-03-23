package com.devpump.mm.materialsmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView tv_scanContent;
    EditText et_itemName, et_itemQuantity;
    Spinner spin_itemQuantityType;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        linkInitalizeComponents();

        getPreviousSavedState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState){
        outState.putString("barCode", tv_scanContent.getText().toString());
        outState.putString("itemName", et_itemName.getText().toString());
        outState.putString("itemQuantity", et_itemQuantity.getText().toString());
        outState.putInt("itemQuantityType",spin_itemQuantityType.getSelectedItemPosition());
    }

    public void getPreviousSavedState(Bundle instanceState){
        if(instanceState != null) {
            tv_scanContent.setText(instanceState.getString("barCode"));
            et_itemName.setText(instanceState.getString("itemName"));
            et_itemQuantity.setText(instanceState.getString("itemQuantity"));
            spin_itemQuantityType.setSelection(instanceState.getInt("itemQuantityType"));
            //Set Edit text cursor to end of line.
            et_itemName.setSelection(et_itemName.getText().length());
        }
    }

    public void linkInitalizeComponents(){
        tv_scanContent = (TextView)findViewById(R.id.tv_barcode);
        et_itemName = (EditText)findViewById(R.id.et_itemName);
        et_itemQuantity = (EditText)findViewById(R.id.et_itemQuantity);
        spin_itemQuantityType = (Spinner) findViewById(R.id.spin_quantityType);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.string_itemQuantities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_itemQuantityType.setAdapter(adapter);
    }


    public void scanBar(View v) {
        try {
            //Start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent scanIntent = new Intent(ACTION_SCAN);
            startActivityForResult(scanIntent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            Toast alertNoScanner = Toast.makeText(Home.this,"No Scanner Found", Toast.LENGTH_LONG);
            alertNoScanner.show();

        }
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String barCode = intent.getStringExtra("SCAN_RESULT");
                tv_scanContent.setText(barCode);
                JSONObject job = new JSONObject();
                try {
                    //Clear text fields
                    et_itemName.setText("");
                    //Create JSON Object for submitting.
                    job.put("actionType","select");
                    job.put("barCode",barCode);
                    checkDatabase(job, new VolleyCallback() {

                        public void onSuccess(JSONObject job) {
                            try {
                                et_itemName.setText(job.getString("itemName").toString().trim());
                                et_itemQuantity.setText(job.getString("itemQuantity").toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }

    public void addItemToDB(View v){

        JSONObject job = new JSONObject();
        try {
            job.put("actionType","insert");
            job.put("barCode",tv_scanContent.getText());
            job.put("itemName",et_itemName.getText());

            checkDatabase(job, new VolleyCallback() {

                public void onSuccess(JSONObject job) {
                    try {
                        Toast toasty = Toast.makeText(Home.this,job.getString("status").toString(), Toast.LENGTH_LONG);
                        toasty.show();
                        Log.v("Insert Status:",job.getString("status").toString());
                        et_itemName.setText("");
                        tv_scanContent.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void checkDatabase(JSONObject jsonPostData, final VolleyCallback callback) throws JSONException { //String barCode, String itemName, double itemQuantity){
        //Create Object request with via POST method with JSON Object.
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.83/main.php",jsonPostData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("JSON Response", response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        });

        // Adding request to request queue
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(req);
    }
}

interface VolleyCallback{
    void onSuccess(JSONObject job);
}