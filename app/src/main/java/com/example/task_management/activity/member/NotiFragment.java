package com.example.task_management.activity.member;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.task_management.R;
import com.example.task_management.adapter.MemberGroupAdapter;
import com.example.task_management.adapter.NotiAdapter;
import com.example.task_management.model.Group;
import com.example.task_management.model.Notification;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotiFragment extends Fragment {
    APIService apiService;
    RecyclerView recyclerView;
    List<Notification> notificationList = new ArrayList<>();
    NotiAdapter notiAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.noti, container, false);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipefreshlayout);
        recyclerView = view.findViewById(R.id.rcv_noti);
        getMyNoti();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyNoti();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private void getMyNoti() {
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");

        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getNoti(authHeader).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    notificationList = response.body();
//                    List<Notification> newListNoti = new ArrayList<>();
//                    for (Notification notification : notificationList) {
//                        if (notification.getId().equals("group_member")) {
//                            newListGroup.add(group);
//                        }
//                    }
                    notiAdapter = new NotiAdapter(getActivity(), notificationList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(notiAdapter);
                    notiAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Log.v("Error code 400", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
