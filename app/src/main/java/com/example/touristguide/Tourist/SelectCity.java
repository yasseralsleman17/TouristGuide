package com.example.touristguide.Tourist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.touristguide.R;

public class SelectCity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    String[] citys = new String[]{"الرياض", "جدة", "مكة المكرمة", "المدينة المنورة", "الأحساء","الدمام", "الطائف", "تبوك", "القطيف", "خميس مشيط", "أبها", "نجران"};
    Spinner spinner;
    String city = "الرياض";
    TextView next;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
        }

        spinner = findViewById(R.id.spinner);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowAllPlaces.class);
                intent.putExtra("city", city);
                intent.putExtra("type", type);
                startActivity(intent);

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, citys);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        city = citys[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}