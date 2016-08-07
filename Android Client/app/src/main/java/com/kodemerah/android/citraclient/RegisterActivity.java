package com.kodemerah.android.citraclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String REGISTER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/register_customer";
    public static final String REGISTER_NAMA = "nama";
    public static final String REGISTER_EMAIL = "email";
    public static final String REGISTER_PHONE = "phone";
    public static final String REGISTER_PASSWORD = "password";

    public EditText txtFullname, txtEmail, txtPhone, txtPassword;

    private String[] register = new String[4];

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtFullname = (EditText) findViewById(R.id.txtName);
        txtEmail= (EditText) findViewById(R.id.txtEmail);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        getSupportActionBar().setTitle("Create New Account");
    }

    public void registerClick(View v){
        register[0] = txtFullname.getText().toString();
        register[1] = txtEmail.getText().toString();
        register[2] = txtPhone.getText().toString();
        register[3] = txtPassword.getText().toString();

        registerLogic();
    }

    private void registerLogic(){
        class RegisterLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, "Registering", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("sukses")){
                    Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
                    i.putExtra("EXTRA_TEXT","Selamat!\nVoucher anda sudah terdaftar!");
                    startActivity(i);
                    finish();
                }else{
                    alert.showAlertDialog(RegisterActivity.this, "Failure..", "Check your connection", false);
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String,String> data = new HashMap<>();
                data.put(REGISTER_NAMA, params[0]);
                data.put(REGISTER_EMAIL, params[1]);
                data.put(REGISTER_PHONE, params[2]);
                data.put(REGISTER_PASSWORD, params[3]);

                String result = rh.sendPostRequest(REGISTER_URL,data);
                if(!result.equals("Error Registering")){
                    result = cancelJSONParse(result);
                }
                return result;
            }
        }

        RegisterLogic lol = new RegisterLogic();
        lol.execute(register);
    }

    private String cancelJSONParse(String myJSON){
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
