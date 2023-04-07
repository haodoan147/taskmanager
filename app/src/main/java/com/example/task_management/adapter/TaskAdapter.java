package com.example.task_management.adapter;

import android.content.Context;
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
        public ImageView imageView;
        public TextView tenSp;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_cate);
            tenSp = itemView.findViewById(R.id.tvNameCategory);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Bạn đã chọn category" + tenSp.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
//        holder.tenSp.setText(task.getName());
//        Glide.with(context).load(task.getImages()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }
}
