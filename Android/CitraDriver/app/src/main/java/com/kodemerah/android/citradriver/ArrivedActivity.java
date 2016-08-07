package com.kodemerah.android.citradriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ArrivedActivity extends AppCompatActivity {


    // Session Manager Class
    SessionManager session;

    public static final String REQUEST_PAY_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/argo_driver";
    public static final String ORDER_ID = "id";
    public static final String ARGO = "argo";
    private String[] datas = new String[2];

    EditText txtArgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrived);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtArgo = (EditText)findViewById(R.id.txtArgo);
    }

    public void requestpayClick(View v){
        Intent i = getIntent();
        datas[0] = i.getStringExtra("EXTRA_ID");
        datas[1] = txtArgo.getText().toString();

        requestPayLogic();


    }

    private void requestPayLogic(){
        class RequestPayLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ArrivedActivity.this, "Getting Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("sukses")) {
                    finish();
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID, params[0]);
                data.put(ARGO, params[1]);


                String result = rh.sendPostRequest(REQUEST_PAY_URL,data);
                if(!result.equals("Error Registering")){
                    result = takeOrderJSONParse(result);
                }
                return result;
            }
        }

        RequestPayLogic lol = new RequestPayLogic();
        lol.execute(datas);
    }

    private String takeOrderJSONParse(String myJSON){
        String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(myJSON);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.optString("status").toString();
                data = status;
            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }

}
