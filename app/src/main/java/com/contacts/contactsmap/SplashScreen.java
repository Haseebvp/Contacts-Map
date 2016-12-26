package com.contacts.contactsmap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import network.CheckConnectivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (CheckConnectivity.isNetworkAvailable(SplashScreen.this)) {
                    Intent main = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }
                else {
                    InternetMessage();
                }
            }
        }, 2000);
    }

    private void InternetMessage(){

        final View v = this.getWindow().getDecorView().findViewById(android.R.id.content);
        final Snackbar snackBar = Snackbar.make(v, "Check Your Internet connection.", Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(Color.parseColor("#FF7373"));
        snackBar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                if (CheckConnectivity.isNetworkAvailable(SplashScreen.this)) {
                    Intent main = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(main);
                    finish();
                } else {
                    InternetMessage();
                    // CustomMessage.getInstance().CustomMessage(this,"Check Your Internet connection.");
                }


            }
        });
        snackBar.show();
    }
}
