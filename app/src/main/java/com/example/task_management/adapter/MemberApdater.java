package com.example.task_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.model.User;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberApdater extends RecyclerView.Adapter<MemberApdater.MyViewHolder>{
    Context context;
    List<User> memberList;
    int groupId;
    public MemberApdater(Context context, List<User> memberList, int groupId) {
        this.context = context;
        this.memberList = memberList;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public MemberApdater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_members, parent, false);
        return new MemberApdater.MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public ImageView iv_options;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_options = itemView.findViewById(R.id.iv_options);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MemberApdater.MyViewHolder holder, int position) {
        User user = memberList.get(position);
        holder.tv_name.setText(user.getName());
        holder.iv_options.setOnClickListener(view -> showPopUpMenu(view, position));
    }
    public void showPopUpMenu(View view,int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.kick_member_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                    String authHeader = "Bearer " + accessToken;
                    APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                    apiService.kickAMember(authHeader,groupId,memberList.get(position).getId()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                        }
                    });
                    removeItem(position);
                    break;
                case R.id.menuUpdate:
            }
            return false;
        });
        popupMenu.show();
    }
    @Override
    public int getItemCount() {
        return memberList == null ? 0 : memberList.size();
    }
    public void removeItem(int position){
        memberList.remove(position);
        notifyItemRemoved(position);
    }
}