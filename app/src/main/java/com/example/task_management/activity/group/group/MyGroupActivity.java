package com.example.task_management.activity.group.group;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.task_management.R;
import com.example.task_management.adapter.GroupAdapter;
import com.example.task_management.model.Group;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupActivity extends AppCompatActivity {
    APIService apiService;
    RecyclerView recyclerView;
    List<Group> listGroup= new ArrayList<>();
    List<Group> newListGroup= new ArrayList<>();
    GroupAdapter groupAdapter;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_group);
        initView();
    }
    private void initView() {
        recyclerView = findViewById(R.id.rcv_group);
        swipeRefreshLayout = findViewById(R.id.swipefreshlayout);
        View create_btn = findViewById(R.id.create_group);
        create_btn.setOnClickListener(view -> {
            showBottomDialogCreate();
        });
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Quản lí nhóm");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getMyGroups();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newListGroup.clear();
                getMyGroups();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void getMyGroups(){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext().getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getMyGroups(authHeader).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    listGroup = response.body();
                    for (Group group: listGroup) {
                        if(group.getRole().equals("group_owner")){
                            newListGroup.add(group);
                        }
                    }
                    groupAdapter = new GroupAdapter(MyGroupActivity.this, newListGroup);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MyGroupActivity.this,
                            2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(groupAdapter);
                    groupAdapter.notifyDataSetChanged();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void showBottomDialogCreate() {
        Activity activity=MyGroupActivity.this;
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_bottom_sheet_layout);

        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.label);
        EditText input_text = dialog.findViewById(R.id.input_text);
        Button btn_submit = dialog.findViewById(R.id.btn_submit);
        title.setText("Tạo nhóm");
        label.setText("Tên nhóm");
        btn_submit.setText("Tạo");
        input_text.setHint("Tên nhóm");
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
                String authHeader = "Bearer " + accessToken;
                APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                apiService.createGroup(authHeader, String.valueOf(input_text.getText())).enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        Group newGroup = response.body();
                        newListGroup.add(newGroup);
                        groupAdapter.notifyItemInserted(newListGroup.size()-1);
                    }
                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                    }
                });

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
