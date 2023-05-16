package com.example.task_management.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.example.task_management.R;
import com.github.ybq.android.spinkit.style.CubeGrid;

public class LoadingDialog {
    Activity activity;
    AlertDialog dialog;
    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_loading, null);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminateDrawable(new CubeGrid());
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(500, 400);
    }
    public void dismissDialog(){
        dialog.dismiss();
    }
}
