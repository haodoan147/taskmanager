package com.example.task_management.activity.group_task;

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

import com.example.task_management.R;
import com.example.task_management.adapter.CategoryAdapter;
import com.example.task_management.adapter.GroupAdapter;
import com.example.task_management.model.Category;
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

public class GroupJoinedFragment extends Fragment {
    APIService apiService;
    RecyclerView recyclerView;
    List<Group> listGroup= new ArrayList<>();
    GroupAdapter groupAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_group, container, false);
        recyclerView = view.findViewById(R.id.rcv_group);
        getMyGroups();
        return view;
    }
    private void getMyGroups(){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getMyGroups(authHeader).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    listGroup = response.body();
                    List<Group> newListGroup= new ArrayList<>();
                    for (Group group: listGroup) {
                        if(group.getRole().equals("group_owner")){
                            newListGroup.add(group);
                        }
                    }
                    groupAdapter = new GroupAdapter(getActivity(), newListGroup);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
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
}
