package com.example.task_management.activity.group.label;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.task_management.R;
import com.example.task_management.adapter.CategoryAdapter;
import com.example.task_management.adapter.LabelApdater;
import com.example.task_management.model.Category;
import com.example.task_management.model.Label;
import com.example.task_management.model.ResponseLabel;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupLabelFragment extends Fragment {
    RecyclerView recyclerView;
    LabelApdater labelAdapter;
    APIService apiService;
    List<Label> labelList= new ArrayList<>();
    int groupId;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.label, container, false);
        recyclerView = view.findViewById(R.id.rcv_label);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipefreshlayout);
        Bundle arguments = getArguments();
        groupId  = arguments.getInt("idGroup",1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                labelList.clear();
                getLabel();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getLabel();
        return view;
    }
    private void getLabel(){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllLabel(authHeader,1,100,"asc",groupId).enqueue(new Callback<ResponseLabel>() {
            @Override
            public void onResponse(Call<ResponseLabel> call, Response<ResponseLabel> response) {
                if (response.isSuccessful()) {
                    labelList.addAll(response.body().getData());
                    labelAdapter = new LabelApdater(getActivity(), labelList);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(labelAdapter);
                    labelAdapter.notifyDataSetChanged();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseLabel> call, Throwable t) {
            }
        });
    }
}
