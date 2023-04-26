package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.AccessToken;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends AppCompatActivity {
    EditText edtTaskName, edtTaskDescription;
    DatePicker deadlineDatePicker;
    String[] items =  {"1"};
    AutoCompleteTextView edtTaskCate,edtTaskLabel,edtTaskPriority;
    ArrayAdapter<String> adapterItems;
    APIService apiService;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_task);
        initView();
//        edtTaskCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
//            }
//        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createtask();
            }
        });
    }
    private void createtask() {
        final String taskName = edtTaskName.getText().toString();
        final String taskDes = edtTaskDescription.getText().toString();
        final String taskCate = edtTaskCate.getText().toString();
        final String taskLabel = edtTaskLabel.getText().toString();
        final String taskPriority = edtTaskPriority.getText().toString();

        if (TextUtils.isEmpty(taskName)) {
            edtTaskName.setError("Vui lòng nhập lại");
            edtTaskName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(taskDes)) {
            edtTaskDescription.setError("Vui lòng nhập lại");
            edtTaskDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskCate)) {
            edtTaskCate.setError("Vui lòng nhập lại");
            edtTaskCate.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskLabel)) {
            edtTaskLabel.setError("Vui lòng nhập lại");
            edtTaskLabel.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskPriority)) {
            edtTaskPriority.setError("Vui lòng nhập lại");
            edtTaskPriority.requestFocus();
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date dueDate = new Date();
        int day = deadlineDatePicker.getDayOfMonth();
        int month = deadlineDatePicker.getMonth();
        int year = deadlineDatePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date dlDate = calendar.getTime();
        int diffInDays = (int) ((dueDate.getTime() - dlDate.getTime())
                / (1000 * 60 * 60 * 24));
        ArrayList<Integer> labels = new ArrayList<Integer>();
        labels.add(Integer.parseInt(taskLabel));
        apiService = RetrofitClient.getInstance().create(APIService.class);
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        apiService.getCreateNewTask(authHeader, taskName, taskDes, "2023-04-22T12:39:53.244Z", Integer.parseInt(taskCate), diffInDays, Integer.parseInt(taskPriority), labels).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Task task = response.body();
                    Toast.makeText(CreateTaskActivity.this, "Create Task Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateTaskActivity.this, TaskActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(CreateTaskActivity.this, "Create Task Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initView(){
        edtTaskName = findViewById(R.id.edittext_task);
        edtTaskDescription =  findViewById(R.id.edittext_description);
        btnSubmit = findViewById(R.id.btn_submit);
        edtTaskCate = findViewById(R.id.edittext_category);
        edtTaskLabel = findViewById(R.id.edittext_label);
        edtTaskPriority = findViewById(R.id.edittext_priority);
        deadlineDatePicker = findViewById(R.id.dlDatePicker);

        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,items);
        edtTaskCate.setAdapter(adapterItems);
        edtTaskLabel.setAdapter(adapterItems);
        edtTaskPriority.setAdapter(adapterItems);
    }
}