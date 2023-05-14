package com.example.task_management.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.group.group.ManageMyGroupActivity;
import com.example.task_management.activity.member.group.DetailGroupActivity;
import com.example.task_management.model.Group;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberGroupAdapter extends RecyclerView.Adapter<MemberGroupAdapter.MyViewHolder>{
    Context context;
    List<Group> groupList;
    public MemberGroupAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public MemberGroupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_group, parent, false);
        return new MemberGroupAdapter.MyViewHolder(view);
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
    public void onBindViewHolder(@NonNull MemberGroupAdapter.MyViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.tv_name.setText(group.getName());
        holder.iv_options.setOnClickListener(view -> showPopUpMenu(view, position));
    }
    public void showPopUpMenu(View view,int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.cate_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDetail:
                    Intent detailContext = new Intent(context, DetailGroupActivity.class);
                    detailContext.putExtra("idGroup", groupList.get(position).getId());
                    context.startActivity(detailContext);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }
}
