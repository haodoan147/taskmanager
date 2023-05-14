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
import com.example.task_management.model.Group;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder>{
    Context context;
    List<Group> groupList;
    public GroupAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_group, parent, false);
        return new GroupAdapter.MyViewHolder(view);
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
    public void onBindViewHolder(@NonNull GroupAdapter.MyViewHolder holder, int position) {
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
                    Intent detailContext = new Intent(context, ManageMyGroupActivity.class);
                    detailContext.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    detailContext.putExtra("idGroup", groupList.get(position).getId());
                    context.startActivity(detailContext);
                    break;
                case R.id.menuDelete:
                    String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                    String authHeader = "Bearer " + accessToken;
                    APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                    apiService.deleteGroup(authHeader,groupList.get(position).getId()).enqueue(new Callback<Group>() {
                        @Override
                        public void onResponse(Call<Group> call, Response<Group> response) {
                        }
                        @Override
                        public void onFailure(Call<Group> call, Throwable t) {
                        }
                    });
                    removeItem(position);
                    break;
                case R.id.menuUpdate:
                    showBottomDialogUpdate(position);
            }
            return false;
        });
        popupMenu.show();
    }
    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }
    public void removeItem(int position){
        groupList.remove(position);
        notifyItemRemoved(position);
    }
    private void showBottomDialogUpdate(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_bottom_sheet_layout);

        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.label);
        EditText input_text = dialog.findViewById(R.id.input_text);
        Button btn_submit = dialog.findViewById(R.id.btn_submit);
        title.setText("Cập nhật");
        label.setText("Tên nhóm");
        btn_submit.setText("Cập nhật");
        input_text.setHint("Tên nhóm");
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                String authHeader = "Bearer " + accessToken;
                APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                apiService.updateGroup(authHeader,groupList.get(position).getId(), String.valueOf(input_text.getText())).enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                    }
                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                    }
                });
                groupList.get(position).setName(String.valueOf(input_text.getText()));
                notifyDataSetChanged();

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
