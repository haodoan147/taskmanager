package com.example.task_management.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.task.DetailTaskActivity;
import com.example.task_management.model.Category;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    enum TaskPriority {
        NONE ,
        LOW ,
        MEDIUM ,
        HIGH ,
        URGENT,
    }
    Context context;
    List<Task> taskList;
    List<Category> listCategory = new ArrayList<>();
    public TaskAdapter(Context context, List<Task> taskList,List<Category> listCategory) {
        this.context = context;
        this.taskList = taskList;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_new, parent, false);
        return new TaskViewHolder(view);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_options;
        public TextView tv_title, tv_des, tv_duration, tv_category,tv_label,tv_priority,tv_status,tv_date,tv_month,tv_assignee;
        public TaskViewHolder(final View itemView) {
            super(itemView);
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

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
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
        holder.tv_priority.setText("Độ ưu tiên: " +String.valueOf(TaskPriority.values()[task.getPriority()]));
        holder.tv_status.setText(task.getStatus());
        holder.iv_options.setOnClickListener(view -> showPopUpMenu(view, position));
        switch (TaskPriority.values()[task.getPriority()])
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
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void removeItem(int position){

        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void setListenerList(List<Task> taskList){
        this.taskList = taskList;
        notifyDataSetChanged();
    }
    public void showPopUpMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.my_group_task_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.Theme_Task_management);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                                String authHeader = "Bearer " + accessToken;
                                APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                                    apiService.deleteTask(authHeader,taskList.get(position).getId()).enqueue(new Callback<Task>() {
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
                    detailContext.putExtra("idTask", taskList.get(position).getId());
                    context.startActivity(detailContext);
                    break;
                case R.id.menuAssign:
                    showBottomDialog();
                    break;

            }
            return false;
        });
        popupMenu.show();
    }
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.assign_bottom_sheet_layout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}
