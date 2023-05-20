package com.example.task_management.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.task_management.R;
import com.example.task_management.model.Category;
import com.example.task_management.model.Task;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StaticticActivity extends AppCompatActivity
{

    ArrayList barArraylist;
    List<Task> taskList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statictis_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Thống kê ");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        taskList = (List<Task>) intent.getSerializableExtra("newTaskList");
        BarChart barChart = findViewById(R.id.barchart);
        getData();
        BarDataSet barDataSet = new BarDataSet(barArraylist,"Thống kê task");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        //color bar data set
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //text color
        barDataSet.setValueTextColor(Color.BLACK);
        //settting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);
    }
    private void getData()
    {
        int TODO = 0;
        int IN_PROGRESS = 0;
        int DONE = 0;
        int POSTPONED = 0;
        int CANCELED = 0;
        barArraylist = new ArrayList();
        if(taskList!=null)
        {
        for (Task task: taskList) {
            if (task.getStatus().equals("TODO")) {
                TODO++;
            }
            if (task.getStatus().equals("IN_PROGRESS")) {
                IN_PROGRESS++;
            }
            if (task.getStatus().equals("DONE")) {
                DONE++;
            }
            if (task.getStatus().equals("POSTPONED")) {
                POSTPONED++;
            }
            if (task.getStatus().equals("CANCELED")) {
                CANCELED++;
            }
        }
        }
        barArraylist.add(new BarEntry(1f,TODO));
        barArraylist.add(new BarEntry(2f,IN_PROGRESS));
        barArraylist.add(new BarEntry(3f,DONE));
        barArraylist.add(new BarEntry(4f,POSTPONED));
        barArraylist.add(new BarEntry(5f,CANCELED));

    }





}
