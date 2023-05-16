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
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.model.Category;
import com.example.task_management.model.Label;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelApdater extends RecyclerView.Adapter<LabelApdater.MyViewHolder>{
    Context context;
    List<Label> labelList;
    public LabelApdater(Context context, List<Label> labelList) {
        this.context = context;
        this.labelList = labelList;
    }

    @NonNull
    @Override
    public LabelApdater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_label, parent, false);
        return new LabelApdater.MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_label;
        public LinearLayout card_cate;
        public ImageView iv_options;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_label = itemView.findViewById(R.id.tv_label);
            card_cate = itemView.findViewById(R.id.card_label_task);
            iv_options = itemView.findViewById(R.id.iv_options);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Bạn đã chọn category" + tv_label.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull LabelApdater.MyViewHolder holder, int position) {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.drawable.status_view_round_1);
        colorCode.add(R.drawable.status_view_round_2);
        colorCode.add(R.drawable.status_view_round_3);
        colorCode.add(R.drawable.status_view_round_4);
        colorCode.add(R.drawable.status_view_round_5);
        Random random = new Random();
        int number = random.nextInt(5);
        Label label = labelList.get(position);
        holder.tv_label.setText(label.getName());
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
                    apiService.deleteLabel(authHeader,labelList.get(position).getId()).enqueue(new Callback<Label>() {
                        @Override
                        public void onResponse(Call<Label> call, Response<Label> response) {
                        }
                        @Override
                        public void onFailure(Call<Label> call, Throwable t) {

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
        return labelList == null ? 0 : labelList.size();
    }
    public void removeItem(int position){

        labelList.remove(position);
        notifyItemRemoved(position);
    }
}
