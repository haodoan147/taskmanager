package com.example.task_management.activity.task;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.task_management.R;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateTask;
import com.example.task_management.model.Label;
import com.example.task_management.model.Task;
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

public class CreateTaskFragment extends Fragment {
    EditText edtTaskName, edtTaskDescription;
    DatePicker deadlineDatePicker;
    ArrayList<String> labelItems  = new ArrayList<>();
    ArrayList<String> cateItems  = new ArrayList<>();
    ArrayList<String> priorityItems  = new ArrayList<>();
    AutoCompleteTextView edtTaskCate,edtTaskLabel,edtTaskPriority;
    ArrayAdapter<String> adapterItems;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    List<Label> listLabel = new ArrayList<>();
    List<Category> listCategory = new ArrayList<>();
    public enum Priority
    {
        NONE, LOW, MEDIUM, HIGH, URGENT
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_task, container, false);
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        authHeader = "Bearer " + accessToken;
        priorityItems.add("NONE");
        priorityItems.add("LOW");
        priorityItems.add("MEDIUM");
        priorityItems.add("HIGH");
        priorityItems.add("URGENT");
        edtTaskName = view.findViewById(R.id.edittext_task);
        edtTaskDescription =  view.findViewById(R.id.edittext_description);
        btnSubmit = view.findViewById(R.id.btn_submit);
        edtTaskCate = view.findViewById(R.id.edittext_category);
        edtTaskLabel = view.findViewById(R.id.edittext_label);
        edtTaskPriority = view.findViewById(R.id.edittext_priority);
        deadlineDatePicker = view.findViewById(R.id.dlDatePicker);
        getLabel();
        getCategory();
        adapterItems = new ArrayAdapter<String>(view.getContext(),R.layout.dropdown_select_option,cateItems);
        edtTaskCate.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(view.getContext(),R.layout.dropdown_select_option,labelItems);
        edtTaskLabel.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(view.getContext(),R.layout.dropdown_select_option,priorityItems);
        edtTaskPriority.setAdapter(adapterItems);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createtask();
            }
        });
        return view;
    }
    private void createtask() {
        final String taskName = edtTaskName.getText().toString();
        final String taskDes = edtTaskDescription.getText().toString();
        final String taskCate = edtTaskCate.getText().toString();
        final String taskLabel = edtTaskLabel.getText().toString();
        final String taskPriority = edtTaskPriority.getText().toString();
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
        Date dueDate = new Date();
        int day = deadlineDatePicker.getDayOfMonth();
        int month = deadlineDatePicker.getMonth();
        int year = deadlineDatePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date dlDate = calendar.getTime();
        int diffInDays = (int) ((dlDate.getTime() - dueDate.getTime())
                / (1000 * 60 * 60 * 24));
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String isoDate = isoDateFormat.format(dlDate);
        apiService = RetrofitClient.getInstance().create(APIService.class);
        Integer[] newlist = {taskLabelID};
        CreateTask newTask = new CreateTask(taskName, taskDes, isoDate, diffInDays, Arrays.asList(newlist), taskCateID, taskPriorityID);
        try{
            apiService.getCreateNewTask(authHeader, newTask).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Task task = response.body();
                        Toast.makeText(getActivity(), "Create Task Success", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(CreateTaskActivity.this, TaskActivity.class);
//                        startActivity(intent);
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
                    Toast.makeText(getActivity(), "Create Task Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void getLabel(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllLabel(authHeader).enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.isSuccessful()) {
                    listLabel = response.body();
                    for (Label value: listLabel) {
                        labelItems.add(value.getName());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void getCategory(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    listCategory = response.body();
                    for (Category value: listCategory) {
                        cateItems.add(value.getName());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
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
}
