package com.bananabreach.ui.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full transaction list/detail UI omitted for brevity. Wire this up to
        // ApiService.getTransactions()/exportTransactions() to extend the demo.
    }
}
