package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;

public class TestActivity extends AppCompatActivity {
    String[] items =  {"Material","Design","Components","Android","5.0 Lollipop"};
    AutoCompleteTextView edittext_category;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_task);

        edittext_category = findViewById(R.id.edittext_category);

        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,items);
        edittext_category.setAdapter(adapterItems);

        edittext_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
