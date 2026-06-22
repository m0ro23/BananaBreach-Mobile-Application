package com.bananabreach.ui.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Payment flow omitted for brevity. Wire this up to
        // ApiService.createTransaction() to extend the demo.
    }
}
