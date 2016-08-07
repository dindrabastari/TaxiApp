package com.kodemerah.android.citradriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class OrderDetailActivity extends AppCompatActivity {

    CardView cvCost,cvProgress, cvDriver;
    TextView txtLocFrom, txtLocDes;
    Button btnTakeOrder;


    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    public static final String ORDER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/get_order_driver";
    public static final String TAKE_ORDER_URL = "http://dindrabastari.esy.es/citra/index.php/mobile/take_order";
    public static final String ORDER_ID = "id";
    public static final String DRIVER_ID = "id_driver";
    private String orders_id;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String id = i.getStringExtra("EXTRA_ID");
        orders_id = id;

        txtLocFrom = (TextView) findViewById(R.id.item_location_from);
        txtLocDes = (TextView) findViewById(R.id.item_location_destination);

        btnTakeOrder = (Button) findViewById(R.id.btnTakeOrder);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderLogic();
    }

    public void takeorderClick(View v){
        if (btnTakeOrder.getText().equals("TAKE ORDER")){
            takeOrderLogic();
        }else if (btnTakeOrder.getText().equals("ARRIVED")){
            Intent i = new Intent(this, ArrivedActivity.class);
            i.putExtra("EXTRA_ID", orders_id);
            startActivity(i);
        }
    }


    private void takeOrderLogic(){
        class TakeOrderLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderDetailActivity.this, "Getting Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                String[] orderDet = s.split("###");
                String status = orderDet[0];
                if (status.equals("sukses")) {
                    btnTakeOrder.setText("ARRIVED");
                }
            }

            @Override
            protected String doInBackground(String... params) {
                session = new SessionManager(getApplicationContext());
                session.checkLogin();
                HashMap<String, String> user = session.getUserDetails();
                String id_driver = user.get(SessionManager.KEY_ID);

                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID, params[0]);
                data.put(DRIVER_ID, id_driver);


                String result = rh.sendPostRequest(TAKE_ORDER_URL,data);
                if(!result.equals("Error Registering")){
                    result = takeOrderJSONParse(result);
                }
                return result;
            }
        }

        TakeOrderLogic lol = new TakeOrderLogic();
        lol.execute(orders_id);
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


    private void orderLogic(){
        class OrderLogic extends AsyncTask<String,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderDetailActivity.this, "Getting Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                String[] orderDet = s.split("###");
                if (orderDet.length>1) {
                    String id_pemesanan = orderDet[0];
                    String lokasi_jemput = orderDet[1];
                    String lokasi_tujuan = orderDet[2];
                    String status_pemesanan = orderDet[3];


                    if (status_pemesanan.equals("BELUM TO")) {
                        btnTakeOrder.setText("TAKE ORDER");
                    } else if (status_pemesanan.equals("SUDAH TO")) {
                        btnTakeOrder.setText("ARRIVED");
                    } else if (status_pemesanan.equals("SAMPAI")) {
                        btnTakeOrder.setVisibility(View.GONE);
                    } else {
                        btnTakeOrder.setVisibility(View.GONE);
                    }

                    txtLocFrom.setText(lokasi_jemput);
                    txtLocDes.setText(lokasi_tujuan);

                    getSupportActionBar().setTitle("#" + id_pemesanan);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String id = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(ORDER_ID, id);

                String result = rh.sendPostRequest(ORDER_URL,data);
                if(!result.equals("Error Registering")){
                    result = JSONParse(result);
                }
                return result;
            }
        }

        OrderLogic lol = new OrderLogic();
        lol.execute(orders_id);
    }

    private String JSONParse(String myJSON){
        String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(myJSON);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.optString("status").toString();
                String status_pemesanan = jsonObject.optString("status_pemesanan").toString();
                String id_pemesanan = jsonObject.optString("id_pemesanan").toString();
                String lokasi_jemput = jsonObject.optString("lokasi_jemput").toString();
                String lokasi_tujuan = jsonObject.optString("lokasi_tujuan").toString();

                if (!status.equals("sukses")){
                    data = status;
                }else{
                    data = id_pemesanan + "###" + lokasi_jemput + "###" + lokasi_tujuan + "###" + status_pemesanan;
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
        return data;
    }

}
