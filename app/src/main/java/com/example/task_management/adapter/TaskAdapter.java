package com.example.task_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.task.ActionDialog;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        public TaskViewHolder(final View itemView) {
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
            iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog();
                    String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
                    String authHeader = "Bearer " + accessToken;
                    APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                        apiService.deleteTask(authHeader,taskList.get(getAdapterPosition()).getId()).enqueue(new Callback<Task>() {
                            @Override
                            public void onResponse(Call<Task> call, Response<Task> response) {
                            }
                            @Override
                            public void onFailure(Call<Task> call, Throwable t) {

                            }
                        });
                    removeItem(getAdapterPosition());
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

    public void removeItem(int position){

        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void openDialog() {
        ActionDialog dialog = new ActionDialog();
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(),"Expam");
    }
    public void setListenerList(List<Task> taskList){
        this.taskList = taskList;
        notifyDataSetChanged();
    }
}
