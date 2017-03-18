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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

//    String barCode, itemName, itemQuantity;
//    int itemSelectedPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        linkComponents();
//        initializeGlobalVariables();

        adapter = ArrayAdapter.createFromResource(
                this, R.array.string_itemQuantities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_itemQuantityType.setAdapter(adapter);

        getSavedState(savedInstanceState);
    }
//    public void initializeGlobalVariables(){
//        barCode = "";
//        itemName = "";
//        itemQuantity = "";
//        itemSelectedPosition = 0;
//    }

    protected void onSaveInstanceState(Bundle outState){
        outState.putString("barCode", tv_scanContent.getText().toString());
        outState.putString("itemName", et_itemName.getText().toString());
        outState.putString("itemQuantity", et_itemQuantity.getText().toString());
        outState.putInt("itemQuantityType",spin_itemQuantityType.getSelectedItemPosition());
    }

    public void linkComponents(){
        tv_scanContent = (TextView)findViewById(R.id.tv_barcode);
        et_itemName = (EditText)findViewById(R.id.et_itemName);
        et_itemQuantity = (EditText)findViewById(R.id.et_itemQuantity);
        spin_itemQuantityType = (Spinner) findViewById(R.id.spin_quantityType);
    }

    public void getSavedState(Bundle instanceState){
        if(instanceState != null) {
            tv_scanContent.setText(instanceState.getString("barCode"));
            et_itemName.setText(instanceState.getString("itemName"));
            et_itemQuantity.setText(instanceState.getString("itemQuantity"));
            spin_itemQuantityType.setSelection(instanceState.getInt("itemQuantityType"));
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
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();
                tv_scanContent.setText(contents);

            }
        }
    }

}