package com.example.task_management.activity.group;

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
import com.example.task_management.activity.category.CategoryActivity;
import com.example.task_management.adapter.CategoryAdapter;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CateByGroupFragment extends Fragment {
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    APIService apiService;
    List<Category> categoryList;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category, container, false);
        recyclerView = view.findViewById(R.id.rcv_cate);
        getCategory();
        return view;
    }
    private void getCategory(){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        Log.e("123", authHeader);
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    categoryAdapter = new CategoryAdapter(getActivity(), categoryList);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
            }
        });
    }
}
