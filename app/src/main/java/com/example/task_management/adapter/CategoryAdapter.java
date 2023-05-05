package com.example.task_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.task_management.R;
import com.example.task_management.model.Category;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{
    Context context;
    List<Category> categoryList;
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_category;
        public LinearLayout card_cate;
        public ImageView iv_options;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category = itemView.findViewById(R.id.tv_category);
            card_cate = itemView.findViewById(R.id.card_cate_task);
            iv_options = itemView.findViewById(R.id.iv_options);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Bạn đã chọn category" + tv_category.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.drawable.status_view_round_1);
        colorCode.add(R.drawable.status_view_round_2);
        colorCode.add(R.drawable.status_view_round_3);
        colorCode.add(R.drawable.status_view_round_4);
        colorCode.add(R.drawable.status_view_round_5);
        Random random = new Random();
        int number = random.nextInt(5);
        Category category = categoryList.get(position);
        holder.tv_category.setText(category.getName());
        holder.card_cate.setBackgroundResource(colorCode.get(number));
        holder.iv_options.setOnClickListener(view -> showPopUpMenu(view, position));
    }
    public void showPopUpMenu(View view,int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.cate_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                    String authHeader = "Bearer " + accessToken;
                    APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                    apiService.deleteCate(authHeader,categoryList.get(position).getId()).enqueue(new Callback<Category>() {
                        @Override
                        public void onResponse(Call<Category> call, Response<Category> response) {
                        }
                        @Override
                        public void onFailure(Call<Category> call, Throwable t) {

                        }
                    });
                    removeItem(position);
                    break;

            }
            return false;
        });
        popupMenu.show();
    }
    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }
    public void removeItem(int position){

        categoryList.remove(position);
        notifyItemRemoved(position);
    }
}
