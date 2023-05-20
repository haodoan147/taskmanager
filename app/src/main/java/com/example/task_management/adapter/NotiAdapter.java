package com.example.task_management.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.member.group.DetailGroupActivity;
import com.example.task_management.model.Group;
import com.example.task_management.model.Notification;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.MyViewHolder>{
    Context context;
    List<Notification> notiList;
    public NotiAdapter(Context context, List<Notification> notiList) {
        this.context = context;
        this.notiList = notiList;
    }

    @NonNull
    @Override
    public NotiAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_noti, parent, false);
        return new NotiAdapter.MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_type,tv_message,tv_date,tv_month;
        public ImageView iv_mark;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_month = itemView.findViewById(R.id.tv_month);
            iv_mark = itemView.findViewById(R.id.iv_mark);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NotiAdapter.MyViewHolder holder, int position) {
        Notification notification = notiList.get(position);
        holder.tv_type.setText(notification.getType());
        holder.tv_message.setText(notification.getMessage());
        holder.tv_date.setText(notification.getCreatedAt().substring(8,10));
        holder.tv_month.setText(notification.getCreatedAt().substring(5,7));
        holder.iv_mark.setOnClickListener(view -> getMyNoti(position));
    }
    @Override
    public int getItemCount() {
        return notiList == null ? 0 : notiList.size();
    }
    public void removeItem(int position){
        notiList.remove(position);
        notifyItemRemoved(position);
    }
    private void getMyNoti(int position) {
        SharedPreferences pref = context.getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");

        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.markNotiRead(authHeader,notiList.get(position).getId()).enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    removeItem(position);
                } else {
                    try {
                        Log.v("Error code 400", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
