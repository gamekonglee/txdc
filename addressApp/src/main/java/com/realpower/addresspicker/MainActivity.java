package com.realpower.addresspicker;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void four(View view) {
        Intent intent = new Intent(this, FourPickerActivity.class);
        startActivity(intent);
    }

    public void three(View view) {
        Intent intent = new Intent(this, ThreePickerActivity.class);
        startActivity(intent);

    }
}
