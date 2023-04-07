package com.example.task_management.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.CategoryAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    APIService apiService;
    List<Category> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        anhXa();
        getCategory();
    }

    private void anhXa (){
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getCategory(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getCategoriesAll().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    categoryAdapter = new CategoryAdapter(CategoryActivity.this, categoryList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
