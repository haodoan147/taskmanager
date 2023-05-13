package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.model.AccessToken;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;

    EditText editTextConFirmPassword;
    Button btnRegister;
    TextView loginRedirectText;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_new);
        initView();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRegister();
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void testRegister() {
        final String username = editTextName.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String confirmPassword = editTextConFirmPassword.getText().toString();
        Log.e("1233", password);
        Log.e("1234", confirmPassword);
        if (TextUtils.isEmpty(username)) {
            editTextName.setError("Please enter your username");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConFirmPassword.setError("Please enter your confirm password");
            editTextConFirmPassword.requestFocus();
            return;
        }
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAccessTokenRegister(email,password,confirmPassword,username,username).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken accessToken = response.body();
                if (response.isSuccessful()) {
                    if (accessToken != null) {
                        Toast.makeText(SignUpActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(accessToken.getAccessToken());
                        Intent intent = new Intent(SignUpActivity.this, LoadingActivity.class);
                        intent.putExtra("currentContext", "HomeActivity");
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "This account has already exist", Toast.LENGTH_SHORT).show();
                    }
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
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Register Failure", Toast.LENGTH_SHORT).show();
            }
        });



    }
    private void initView(){
        editTextName =  findViewById(R.id.edittext_name);
        editTextEmail =  findViewById(R.id.edittext_email);
        editTextPassword =  findViewById(R.id.edittext_password);
        editTextConFirmPassword =  findViewById(R.id.edittext_confirm_password);
        btnRegister= findViewById(R.id.imgBtn_Register);
        loginRedirectText = findViewById(R.id.loginRedirectText);
    }
}
