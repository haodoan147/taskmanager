package com.example.task_management.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.task_management.R;
import com.example.task_management.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    Context context;
    List<Task> taskList;
    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_remove, iv_detail;
        public TextView tv_cate, tv_task, tv_deadline, tv_priority;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_remove = itemView.findViewById(R.id.iv_remove);
            iv_detail = itemView.findViewById(R.id.iv_detail);
            tv_cate = itemView.findViewById(R.id.tv_category);
            tv_task = itemView.findViewById(R.id.tv_task);
            tv_deadline = itemView.findViewById(R.id.tv_deadline);
            tv_priority = itemView.findViewById(R.id.tv_priority);
            iv_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Bạn đã chọn task" + tv_task.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tv_cate.setText(String.valueOf(task.getCategoryId()));
        holder.tv_task.setText(task.getTitle());
        holder.tv_deadline.setText(task.getDueDate());
        holder.tv_priority.setText(String.valueOf(task.getCategoryId()));
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }
}
