/**
 * Created by Handpoint on 09/02/15.
 * Copyright (c) 2014 Handpoint. All rights reserved.
 */

package com.handpoint.androidGettingStarted;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity extends Activity {
    private Button connectToSimulator;
    private Button connectToDevice;
    private Button payWithPinAuthorizedButton;
    MyClass myClass;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myClass = new MyClass(this);
        initializeButtons();
    }

    public void initializeButtons() {
        connectToSimulator = (Button) findViewById(R.id.connectSimulator);
        connectToSimulator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myClass.connectToSimulator();
            }
        });
        connectToDevice = (Button) findViewById(R.id.connectDevice);
        connectToDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myClass.connectToDevice();
            }
        });
        payWithPinAuthorizedButton = (Button) findViewById(R.id.payWithPinAuthorized);
        payWithPinAuthorizedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myClass.payWithPinAuthorized();
            }
        });
    }

    // Function that displays a receipt in an alert dialog
    public void callReceiptDialog(final String receipt) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String positiveButton = "OK";
                WebView webView = new WebView(MyActivity.this); // Create a webView to display the receipt
                webView.loadData(receipt, "text/html", "UTF-8");
                webView.getSettings().setDefaultFontSize(20);
                webView.setVerticalScrollBarEnabled(true);
                new AlertDialog.Builder(MyActivity.this)// Defines an AlertDialog that will popup
                        .setTitle("Transaction result")
                        .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setView(webView)
                        .show();
            }
        });
    }

    // Function that updates a small label to display the current transaction/connection status
    public void updateStatusDisplay(final String toThis) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) findViewById(R.id.statusDisplay);
                textView.setText(toThis);
            }
        });
    }


    // The Android lifecycle helper functions
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        myClass.disconnect(); // disconnects from the card reader if the application is paused
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // When the user clicks the back button of the smartphone the application is moved in the background
    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
    }

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        initializeButtons(); // initialize the buttons again after restarting the app
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
