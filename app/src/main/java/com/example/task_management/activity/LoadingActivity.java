package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.activity.group_task.GroupTaskActivity;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String currentAct = intent.getStringExtra("currentContext");
                switch (currentAct){
                    case "GroupActivity":
                        startActivity(new Intent(getApplicationContext(), GroupTaskActivity.class));
                        break;
                    case "HomeActivity":
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        break;
                }
                finish();
            }
        },3000);
    }
}
