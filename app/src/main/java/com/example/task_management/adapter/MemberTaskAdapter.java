package com.example.task_management.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.LoadingDialog;
import com.example.task_management.activity.task.DetailTaskActivity;
import com.example.task_management.model.Category;
import com.example.task_management.model.Task;
import com.example.task_management.model.User;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;
import com.woxthebox.draglistview.DragItemAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberTaskAdapter extends DragItemAdapter<Pair<Long, Task>, MemberTaskAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    enum TaskPriority {
        NONE ,
        LOW ,
        MEDIUM ,
        HIGH ,
        URGENT,
    }
    Context context;
    List<Category> listCategory;
    public MemberTaskAdapter(ArrayList<Pair<Long, Task>> list, int layoutId, int grabHandleId, boolean dragOnLongPress,Context context, List<Category> cateList) {
        this.context = context;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.listCategory = cateList;
        setItemList(list);
    }

    @NonNull
    @Override
    public MemberTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new MemberTaskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberTaskAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Task task = mItemList.get(position).second;
        holder.tv_date.setText(task.getDueDate().substring(8,10));
        holder.tv_month.setText(task.getDueDate().substring(5,7));
        holder.tv_title.setText(task.getTitle());
        holder.tv_des.setText(task.getDescription());
        holder.tv_duration.setText("Hạn: " +String.valueOf(task.getDuration()) + "ngày");
        for (Category cate: listCategory) {
            if(cate.getId() == task.getCategoryId()) {
                holder.tv_category.setText("Loại: "+cate.getName());
                break;
            }
        }
        holder.tv_label.setText((("Nhãn: "+ (task.getLabels()).get(0).getName())));
        holder.tv_priority.setText("Độ ưu tiên: " +String.valueOf(TaskAdapter.TaskPriority.values()[task.getPriority()]));
        holder.tv_status.setText(task.getStatus());
        holder.tv_status.setOnClickListener(view -> showChangeStatusPopUpMenu(view, position, holder.tv_status));
        holder.iv_options.setOnClickListener(view -> showPopUpMenu(view, position));
        switch (TaskAdapter.TaskPriority.values()[task.getPriority()])
        {
            case NONE:
                holder.tv_priority.setBackgroundResource(R.drawable.status_view_round_1);
                break;
            case LOW:
                holder.tv_priority.setBackgroundResource(R.drawable.status_view_round_2);
                break;
            case MEDIUM:
                holder.tv_priority.setBackgroundResource(R.drawable.status_view_round_3);
                break;
            case HIGH:
                holder.tv_priority.setBackgroundResource(R.drawable.status_view_round_4);
                break;
            case URGENT:
                holder.tv_priority.setBackgroundResource(R.drawable.status_view_round_5);
                break;
        }
        switch (task.getStatus())
        {
            case "TODO":
                holder.tv_status.setBackgroundResource(R.drawable.status_view_round_1);
                break;
            case "IN_PROGRESS":
                holder.tv_status.setBackgroundResource(R.drawable.status_view_round_2);
                break;
            case "DONE":
                holder.tv_status.setBackgroundResource(R.drawable.status_view_round_3);
                break;
            case "POSTPONED":
                holder.tv_status.setBackgroundResource(R.drawable.status_view_round_4);
                break;
            case "CANCELED":
                holder.tv_status.setBackgroundResource(R.drawable.status_view_round_5);
                break;
        }
        if(task.getAssignee()==null){
            holder.tv_assignee.setText("Người thực hiện: chưa có");
        }
        else{
            holder.tv_assignee.setText("Người thực hiện: "+ task.getAssignee().getName());
        }
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        public ImageView iv_options;
        public TextView tv_title, tv_des, tv_duration, tv_category,tv_label,tv_priority,tv_status,tv_date,tv_month,tv_assignee;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            iv_options = itemView.findViewById(R.id.iv_options);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_des = itemView.findViewById(R.id.tv_des);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_label = itemView.findViewById(R.id.tv_label);
            tv_priority = itemView.findViewById(R.id.tv_priority);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_assignee = itemView.findViewById(R.id.tv_assignee);
        }
    }
    public void showPopUpMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.member_task_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.Theme_Task_management);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                                String authHeader = "Bearer " + accessToken;
                                APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                                apiService.deleteTask(authHeader,mItemList.get(position).second.getId()).enqueue(new Callback<Task>() {
                                    @Override
                                    public void onResponse(Call<Task> call, Response<Task> response) {
                                    }
                                    @Override
                                    public void onFailure(Call<Task> call, Throwable t) {

                                    }
                                });
                                removeItem(position);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuDetail:
                    LoadingDialog loadingDialog = new LoadingDialog((Activity) context);
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Replace the current fragment with NewTaskFragment and pass the parameter
                            Intent detailContext = new Intent(context, DetailTaskActivity.class);
                            detailContext.putExtra("idTask", mItemList.get(position).second.getId());
                            detailContext.putExtra("listCate", (Serializable) listCategory);
                            context.startActivity(detailContext);
                            loadingDialog.dismissDialog();
                        }
                    }, 3000);
                    break;
                case R.id.menuAssign:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
    public void showChangeStatusPopUpMenu(View view, int position, TextView tv_status) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.change_status_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.Theme_Task_management);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                                String authHeader = "Bearer " + accessToken;
                                APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                                apiService.deleteTask(authHeader,mItemList.get(position).second.getId()).enqueue(new Callback<Task>() {
                                    @Override
                                    public void onResponse(Call<Task> call, Response<Task> response) {
                                    }
                                    @Override
                                    public void onFailure(Call<Task> call, Throwable t) {

                                    }
                                });
                                removeItem(position);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuDetail:
                    Intent detailContext = new Intent(context, DetailTaskActivity.class);
                    detailContext.putExtra("idTask", mItemList.get(position).second.getId());
                    context.startActivity(detailContext);
                    break;
                case R.id.menu_status_todo:
                    updateStatus("TODO",mItemList.get(position).second.getId());
                    tv_status.setText("TODO");
                    tv_status.setBackgroundResource(R.drawable.status_view_round_1);
                    break;
                case R.id.menu_status_process:
                    updateStatus("IN_PROGRESS",mItemList.get(position).second.getId());
                    tv_status.setText("IN_PROGRESS");
                    tv_status.setBackgroundResource(R.drawable.status_view_round_2);
                    break;
                case R.id.menu_status_done:
                    updateStatus("DONE",mItemList.get(position).second.getId());
                    tv_status.setText("DONE");
                    tv_status.setBackgroundResource(R.drawable.status_view_round_3);
                    break;
                case R.id.menu_status_postponed:
                    updateStatus("POSTPONED",mItemList.get(position).second.getId());
                    tv_status.setText("POSTPONED");
                    tv_status.setBackgroundResource(R.drawable.status_view_round_4);
                    break;
                case R.id.menu_status_cancled:
                    updateStatus("CANCELED",mItemList.get(position).second.getId());
                    tv_status.setText("CANCELED");
                    tv_status.setBackgroundResource(R.drawable.status_view_round_5);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
    private void updateStatus(String status,Integer id){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.updateStatusTask(authHeader,id,status).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
            }
            @Override
            public void onFailure(Call<Task> call, Throwable t) {

            }
        });
    }
}
