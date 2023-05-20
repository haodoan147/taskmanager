package com.example.task_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.model.Category;
import com.example.task_management.model.Comment;
import com.example.task_management.model.Task;

import java.util.List;

public class CommentApdater extends RecyclerView.Adapter<CommentApdater.MyViewHolder>{
    Context context;
    List<Comment> commentList;
    public CommentApdater(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }
    @NonNull
    @Override
    public CommentApdater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CommentApdater.MyViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tv_username.setText(comment.getOwner().getName());
        holder.tv_des.setText(comment.getContent());
        holder.tv_createAt.setText(comment.getCreatedAt());
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_username,tv_des,tv_createAt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_des = itemView.findViewById(R.id.tv_des);
            tv_createAt = itemView.findViewById(R.id.tv_createAt);
        }
    }
    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }
    public void addItem(int position, Comment item) {
        commentList.add(position, item);
        notifyItemInserted(position);
    }
}
