/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.task_management.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.example.task_management.R;
import com.example.task_management.activity.task.DetailTaskActivity;
import com.example.task_management.model.Category;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemAdapter extends DragItemAdapter<Pair<Long, Task>, ItemAdapter.ViewHolder> {

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
    List<Category> listCategory = new ArrayList<>();

    public ItemAdapter(ArrayList<Pair<Long, Task>> list, int layoutId, int grabHandleId, boolean dragOnLongPress,Context context) {
        this.context = context;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        public ImageView iv_options;
        public TextView tv_title, tv_des, tv_duration, tv_category,tv_label,tv_priority,tv_status,tv_date,tv_month;

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
        }
    }
    public void showPopUpMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.task_option_popup, popupMenu.getMenu());
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
                case R.id.menuDone:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
}
