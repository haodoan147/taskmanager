package com.example.task_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.model.JoinRequest;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.MyViewHolder> {
    Context context;
    List<JoinRequest> joinRequestList;

    public JoinRequestAdapter(Context context, List<JoinRequest> joinRequestList) {
        this.context = context;
        this.joinRequestList = joinRequestList;
    }

    @NonNull
    @Override
    public JoinRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new JoinRequestAdapter.MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name,tv_status,tv_accept,tv_reject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_accept = itemView.findViewById(R.id.tv_accept);
            tv_reject = itemView.findViewById(R.id.tv_reject);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull JoinRequestAdapter.MyViewHolder holder, int position) {
        JoinRequest joinRequest = joinRequestList.get(position);
        holder.tv_name.setText(joinRequest.getRequestBy().getName());
        holder.tv_status.setText("Đang chờ");
        holder.tv_reject.setOnClickListener(view -> {requestReject(position);removeItem(position);});
        holder.tv_accept.setOnClickListener(view -> {requestAccept(position);removeItem(position);});
    }

    @Override
    public int getItemCount() {
        return joinRequestList == null ? 0 : joinRequestList.size();
    }

    public void removeItem(int position) {
        joinRequestList.remove(position);
        notifyItemRemoved(position);
    }
    private void requestReject(int position){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.rejectRequest(authHeader,joinRequestList.get(position).getId()).enqueue(new Callback<JoinRequest>() {
            @Override
            public void onResponse(Call<JoinRequest> call, Response<JoinRequest> response) {
            }
            @Override
            public void onFailure(Call<JoinRequest> call, Throwable t) {

            }
        });
    }
    private void requestAccept(int position){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.acceptRequest(authHeader,joinRequestList.get(position).getId()).enqueue(new Callback<JoinRequest>() {
            @Override
            public void onResponse(Call<JoinRequest> call, Response<JoinRequest> response) {

            }
            @Override
            public void onFailure(Call<JoinRequest> call, Throwable t) {
            }
        });
    }
}