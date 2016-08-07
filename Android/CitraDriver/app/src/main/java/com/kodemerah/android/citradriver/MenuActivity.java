package com.kodemerah.android.citradriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {

    SessionManager session;

    AlertDialogManager alert = new AlertDialogManager();

    String name, email, id;

    Button btnHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(SessionManager.KEY_NAME);
        email = user.get(SessionManager.KEY_EMAIL);
        id = user.get(SessionManager.KEY_ID);

        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OrderActivity.class);
                i.putExtra("EXTRAS", "history");
                startActivity(i);
            }
        });
    }

    public void neworderClick(View v){
        Intent i = new Intent(this, OrderActivity.class);
        i.putExtra("EXTRAS", "new");
        startActivity(i);
    }

    public void signoutClick(View v){
        session.logoutUser();
    }

}
