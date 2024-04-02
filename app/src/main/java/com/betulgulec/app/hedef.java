package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class hedef extends AppCompatActivity {


    private Spinner spinnerperiyot;
    private TextView spinnerperiyottext;

    private ArrayAdapter<CharSequence>adapterperiyot;

   private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hedef);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        Toast.makeText(getApplicationContext(), "Sağlıklı Kal", Toast.LENGTH_SHORT).show();



    }

    public void init(){

        spinnerperiyot=findViewById(R.id.spinnerperiyot);
        spinnerperiyottext=findViewById(R.id.spinnerperiyottext);

        adapterperiyot=ArrayAdapter.createFromResource(this,R.array.spinnerperiyot, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapterperiyot.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerperiyot.setAdapter(adapterperiyot);


    }


    public void yemeklistesi(){
        Intent intent=new Intent(this, menu.class);
        startActivity(intent);
    }




}