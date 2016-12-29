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
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView tv_scanContent;
    EditText et_itemName;
    String barCode, itemName;
    double itemQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        linkComponents();
        initializeGlobalVariables();
        getSavedState(savedInstanceState);
    }
    public void initializeGlobalVariables(){
        barCode = "";
        itemName = "";
        itemQuantity = 0.0;
    }

    protected  void onSaveInstanceState(Bundle outState){
        outState.putString("barCode", tv_scanContent.getText().toString());
        outState.putString("itemName", et_itemName.getText().toString());

    }

    public void linkComponents(){
        tv_scanContent = (TextView)findViewById(R.id.tv_barcode);
        et_itemName = (EditText)findViewById(R.id.et_itemName);
    }

    public void getSavedState(Bundle instanceState){
        if(instanceState != null) {
            barCode = instanceState.getString("barCode");
            itemName = instanceState.getString("itemName");
            if(barCode != null) {
                tv_scanContent.setText(barCode);
            }
            if(itemName != null){
                et_itemName.setText(itemName);
            }
        }
    }


    public void scanBar(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            //intent.putExtra("SCAN_MODE"); Ignore specific mode type.
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(Home.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
//                toast.show();

                testPost(new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        barCode = result;
                        tv_scanContent.setText(barCode);
                        et_itemName.setText(itemName);
                    }
                },contents);

            }
        }
    }

    public void onResume(){
        super.onResume();
    }


    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public void testPost(final VolleyCallback callback, final String barCode){

        final String url = "http://192.168.1.83:4223";
        final String returnResponse;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        callback.onSuccess(response.toString());
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("barCode", barCode);
                params.put("itemName",itemName);

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);

    }

}