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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.example.task_management.R;
import com.example.task_management.activity.HomeActivity;
import com.example.task_management.activity.LoadingDialog;
import com.example.task_management.activity.group.task.MyGroupUpdateTaskActivity;
import com.example.task_management.activity.task.DetailTaskActivity;
import com.example.task_management.model.Assignee;
import com.example.task_management.model.Category;
import com.example.task_management.model.Label;
import com.example.task_management.model.ResponseCate;
import com.example.task_management.model.ResponseLabel;
import com.example.task_management.model.Task;
import com.example.task_management.model.UnAssignee;
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

public class GroupTaskAdapter extends DragItemAdapter<Pair<Long, Task>, GroupTaskAdapter.ViewHolder> {

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
    List<User> memberList= new ArrayList<>();
    List<Category> listCategoryForUpdate = new ArrayList<>();
    List<Label> listLabelForUpdate = new ArrayList<>();
    int idGroup;
    public GroupTaskAdapter(ArrayList<Pair<Long, Task>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Context context, List<Category> cateList, List<User> memberList, int idGroup) {
        this.context = context;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.listCategory = cateList;
        this.memberList = memberList;
        this.idGroup = idGroup;
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
        popupMenu.getMenuInflater().inflate(R.menu.my_group_task_option, popupMenu.getMenu());
        LoadingDialog loadingDialog = new LoadingDialog((Activity) context);
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
                    showBottomDialog(position);
                    break;
                case R.id.menuUnAssign:
                    unAssignTask(mItemList.get(position).second.getId(),1);
                    break;
                case R.id.menuUpdate:
                    loadingDialog.startLoadingDialog();
                    getLabel();
                    getCategory();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent detailContext = new Intent(context, MyGroupUpdateTaskActivity.class);
                            detailContext.putExtra("oldTask", mItemList.get(position).second);
                            detailContext.putExtra("newCateList", (Serializable) listCategoryForUpdate);
                            detailContext.putExtra("newLabelList", (Serializable) listLabelForUpdate);
                            context.startActivity(detailContext);
                            loadingDialog.dismissDialog();
                        }
                    }, 3000);
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
    private void showBottomDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.assign_bottom_sheet_layout);
        Spinner spinner = dialog.findViewById(R.id.assignee_input);
        Button addBtn = dialog.findViewById(R.id.assignTask);
        List<String> memberNamelist = new ArrayList<>();
        for (User user: memberList) {
            memberNamelist.add(user.getName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(dialog.getContext(), android.R.layout.simple_spinner_item,memberNamelist);
        addBtn.setOnClickListener(new View.OnClickListener() {
            int userId;
            @Override
            public void onClick(View v) {
                for (User user: memberList) {
                    if(user.getName().equals(spinner.getSelectedItem().toString())){
                        userId = user.getId();
                    }
                }
                dialog.dismiss();
                assignTask(mItemList.get(position).second.getId(),idGroup, userId);
            }
        });
        spinner.setAdapter(arrayAdapter);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void assignTask(int id, int groupId, int userId){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        Assignee assignee = new Assignee(groupId,userId);
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.asignTask(authHeader,id,assignee).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã gán thành công", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(context, "Có thành viên đã làm task này rồi", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Task> call, Throwable t) {
            }
        });
    }
    private void unAssignTask(int id, int groupId){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        UnAssignee unassignee = new UnAssignee(groupId);
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.unAsignTask(authHeader,id,unassignee).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã bỏ gán thành công", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Bỏ gán thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Task> call, Throwable t) {
            }
        });
    }
    private void getLabel(){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllLabel(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseLabel>() {
            @Override
            public void onResponse(Call<ResponseLabel> call, Response<ResponseLabel> response) {
                if (response.isSuccessful()) {
                    listLabelForUpdate.addAll(response.body().getData());
                }
                else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseLabel> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void getCategory(){
        String accessToken = (SharedPrefManager.getInstance(context.getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseCate>() {
            @Override
            public void onResponse(Call<ResponseCate> call, Response<ResponseCate> response) {
                if (response.isSuccessful()) {
                    listCategoryForUpdate.addAll(response.body().getData());
                }
                else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseCate> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
