package com.example.task_management.activity.member.group;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.SignInActivity;
import com.example.task_management.adapter.GroupAdapter;
import com.example.task_management.adapter.MemberGroupAdapter;
import com.example.task_management.model.Group;
import com.example.task_management.model.JoinRequest;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupFragment extends Fragment {
    APIService apiService;
    RecyclerView recyclerView;
    List<Group> listGroup= new ArrayList<>();
    MemberGroupAdapter groupAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_group_as_member, container, false);
        recyclerView = view.findViewById(R.id.rcv_group);
        View create_btn = view.findViewById(R.id.create_group);
        create_btn.setOnClickListener(view1 -> {
            showBottomDialogCreate();
        });
        getMyGroups();
        return view;
    }
    private void getMyGroups(){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
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
                        if(group.getRole().equals("group_member")){
                            newListGroup.add(group);
                        }
                    }
                    groupAdapter = new MemberGroupAdapter(getActivity(), newListGroup);
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
    private void showBottomDialogCreate() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_bottom_sheet_layout);

        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.label);
        EditText input_text = dialog.findViewById(R.id.input_text);
        Button btn_submit = dialog.findViewById(R.id.btn_submit);
        title.setText("Tham gia vào nhóm");
        label.setText("ID của nhóm");
        btn_submit.setText("Yêu cầu");
        input_text.setHint("ID nhóm");
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = -1;
                    temp = Integer.parseInt(String.valueOf(input_text.getText()));

                if(temp!=-1)
                {
                    dialog.dismiss();
                    String accessToken = (SharedPrefManager.getInstance(getActivity()).getAccessToken()).getAccessToken();
                    String authHeader = "Bearer " + accessToken;
                    APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                    apiService.requestGroup(authHeader,temp).enqueue(new Callback<JoinRequest>() {
                        @Override
                        public void onResponse(Call<JoinRequest> call, Response<JoinRequest> response) {
                            Toast.makeText(getActivity(), "Yêu cầu thành công", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<JoinRequest> call, Throwable t) {
                            Toast.makeText(getActivity(), "Yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    input_text.setError("Vui lòng nhập lại");
                    input_text.requestFocus();
                    return;
                }

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
