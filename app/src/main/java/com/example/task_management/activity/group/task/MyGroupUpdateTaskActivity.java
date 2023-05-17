package com.example.task_management.activity.group.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.task_management.R;
import com.example.task_management.activity.HomeActivity;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateTask;
import com.example.task_management.model.Label;
import com.example.task_management.model.ResponseCate;
import com.example.task_management.model.ResponseLabel;
import com.example.task_management.model.Task;
import com.example.task_management.model.UpdateTask;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupUpdateTaskActivity extends AppCompatActivity {
    EditText edtTaskName, edtTaskDescription,edtTaskDuration;
    DatePicker deadlineDatePicker;
    ArrayList<String> labelItems  = new ArrayList<>();
    ArrayList<String> cateItems  = new ArrayList<>();
    ArrayList<String> priorityItems  = new ArrayList<>();
    ArrayList<String> statusItems  = new ArrayList<>();
    AutoCompleteTextView edtTaskCate,edtTaskLabel,edtTaskPriority, edtTaskStatus;
    ArrayAdapter<String> adapterItems;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    List<Label> listLabel = new ArrayList<>();
    List<Category> listCategory = new ArrayList<>();
    ImageView btnBack;
    Toolbar toolbar;
    public enum Priority
    {
        NONE, LOW, MEDIUM, HIGH, URGENT
    }
    int idGroup;
    Task oldTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_new_task);
        Intent intent = getIntent();
        oldTask = (Task) intent.getSerializableExtra("oldTask");
        idGroup = intent.getIntExtra("idGroup", 1);
        listCategory = (List<Category>) intent.getSerializableExtra("newCateList");
        listLabel = (List<Label>) intent.getSerializableExtra("newLabelList");
        initView();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask();
            }
        });
    }
    private void updateTask() {
        final String taskName = edtTaskName.getText().toString();
        final String taskDes = edtTaskDescription.getText().toString();
        final String taskCate = edtTaskCate.getText().toString();
        final String taskLabel = edtTaskLabel.getText().toString();
        final String taskPriority = edtTaskPriority.getText().toString();
        final String taskStatus = edtTaskStatus.getText().toString();
        final String taskDuration = edtTaskDuration.getText().toString();
        final int taskCateID = getCateId();
        final int taskLabelID = getLabelId();
        final int taskPriorityID = getPriorityId();

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
        if (TextUtils.isEmpty(taskStatus)) {
            edtTaskStatus.setError("Vui lòng nhập lại");
            edtTaskStatus.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskDuration)) {
            edtTaskDuration.setError("Vui lòng nhập lại");
            edtTaskDuration.requestFocus();
            return;
        }
        int day = deadlineDatePicker.getDayOfMonth();
        int month = deadlineDatePicker.getMonth();
        int year = deadlineDatePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date dlDate = calendar.getTime();
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String isoDate = isoDateFormat.format(dlDate);
        apiService = RetrofitClient.getInstance().create(APIService.class);
        Integer[] newlist = {taskLabelID};
        UpdateTask newTask = new UpdateTask(taskStatus,taskName, taskDes, isoDate, Integer.parseInt(taskDuration), Arrays.asList(newlist), taskCateID, taskPriorityID);
        try{
            apiService.updateTask(authHeader,oldTask.getId(), newTask).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Task task = response.body();
                        Toast.makeText(MyGroupUpdateTaskActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MyGroupUpdateTaskActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }else{
                        try {
                            Log.v("Error code 400",response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Toast.makeText(MyGroupUpdateTaskActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cập nhật task");
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        authHeader = "Bearer " + accessToken;
        for (Category value: listCategory) {
            cateItems.add(value.getName());
        }
        for (Label value: listLabel) {
            labelItems.add(value.getName());
        }
        priorityItems.add("NONE");
        priorityItems.add("LOW");
        priorityItems.add("MEDIUM");
        priorityItems.add("HIGH");
        priorityItems.add("URGENT");
        statusItems.add("TODO");
        statusItems.add("IN_PROGRESS");
        statusItems.add("DONE");
        statusItems.add("POSTPONED");
        statusItems.add("CANCELED");
        edtTaskName = findViewById(R.id.edittext_task);
        edtTaskDescription =  findViewById(R.id.edittext_description);
        btnSubmit = findViewById(R.id.btn_submit);
        edtTaskCate = findViewById(R.id.edittext_category);
        edtTaskLabel = findViewById(R.id.edittext_label);
        edtTaskPriority = findViewById(R.id.edittext_priority);
        edtTaskStatus = findViewById(R.id.edittext_status);
        edtTaskDuration  = findViewById(R.id.edittext_duration);
        deadlineDatePicker = findViewById(R.id.dlDatePicker);
        for (Category value: listCategory) {
            cateItems.add(value.getName());
        }
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,cateItems);
        edtTaskCate.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,labelItems);
        edtTaskLabel.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,priorityItems);
        edtTaskPriority.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,statusItems);
        edtTaskStatus.setAdapter(adapterItems);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtTaskName.setText(oldTask.getTitle());
        edtTaskDescription.setText(oldTask.getDescription());
        edtTaskCate.setText(getCateName());
        edtTaskLabel.setText(oldTask.getLabels().get(0).getName());
        edtTaskPriority.setText(String.valueOf(Priority.values()[oldTask.getPriority()]));
        edtTaskStatus.setText(oldTask.getStatus());
        edtTaskDuration.setText(String.valueOf(oldTask.getDuration()));
    }
    private void getLabel(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllLabel(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseLabel>() {
            @Override
            public void onResponse(Call<ResponseLabel> call, Response<ResponseLabel> response) {
                if (response.isSuccessful()) {
                    listLabel.addAll(response.body().getData());
                    Log.e("Error code 400",listLabel.get(0).getName());
                    for (Label value: listLabel) {
                        labelItems.add(value.getName());
                    }

                }
                else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseLabel> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void getCategory(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseCate>() {
            @Override
            public void onResponse(Call<ResponseCate> call, Response<ResponseCate> response) {
                if (response.isSuccessful()) {
                    listCategory.addAll(response.body().getData());
                    for (Category value: listCategory) {
                        cateItems.add(value.getName());
                    }
                }
                else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseCate> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private Integer getLabelId(){
        int selectedId = 0;
        for (Label value: listLabel) {
            if(value.getName().contentEquals(edtTaskLabel.getText().toString()))
                selectedId = value.getId();
        }
        return selectedId;
    }
    private Integer getCateId(){
        int selectedId = 0;
        for (Category value: listCategory) {
            if(value.getName().contentEquals(edtTaskCate.getText().toString()))
                selectedId = value.getId();
        }
        return selectedId;
    }
    private Integer getPriorityId(){
        int selectedId = Priority.valueOf(edtTaskPriority.getText().toString()).ordinal();;
        return selectedId;
    }

    private String getCateName(){
        int selectedId = 0;
        for (Category value: listCategory) {
            if(value.getId() == oldTask.getCategoryId())
                return value.getName();
        }
        return null;
    }
}