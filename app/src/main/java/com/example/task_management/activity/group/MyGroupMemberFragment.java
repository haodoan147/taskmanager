package com.example.task_management.activity.group;

import android.content.Context;
import android.content.Intent;
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

import com.example.task_management.R;
import com.example.task_management.activity.SignInActivity;
import com.example.task_management.adapter.GroupAdapter;
import com.example.task_management.adapter.MemberApdater;
import com.example.task_management.model.Group;
import com.example.task_management.model.User;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupMemberFragment extends Fragment {
    APIService apiService;
    RecyclerView recyclerView;
    List<User> memberList= new ArrayList<>();
    int groupId;
    MemberApdater memberApdater;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_group_member, container, false);
        recyclerView = view.findViewById(R.id.rcv_group);
        Bundle arguments = getArguments();
        groupId  = arguments.getInt("idGroup");
        View create_btn = view.findViewById(R.id.show_join_request);
        create_btn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), GroupJoinRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("idGroup", groupId);
            startActivity(intent);
        });
        getMyGroupMembers();
        return view;
    }
    private void getMyGroupMembers(){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        Log.e("123", String.valueOf(groupId));
        apiService.getAllMembers(authHeader,groupId).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    memberList = response.body();
                    memberApdater = new MemberApdater(getActivity(), memberList, groupId);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(memberApdater);
                    memberApdater.notifyDataSetChanged();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
