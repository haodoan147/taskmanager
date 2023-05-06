package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.model.AccessToken;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;

    EditText editTextConFirmPassword;
    ImageButton btnRegister;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        initView();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRegister();
            }
        });

    }

    private void testRegister() {
        final String username = editTextName.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String confirmPassword = editTextConFirmPassword.getText().toString();

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

        apiService.getAccessTokenRegister(email, password,confirmPassword,username,username).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken accessToken = response.body();

                if (accessToken != null) {
                    Toast.makeText(SignUpActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(accessToken.getAccessToken());
                    Intent intent = new Intent(SignUpActivity.this, LoadingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "This account has already exist", Toast.LENGTH_SHORT).show();
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
        editTextConFirmPassword =  findViewById(R.id.edittext_email);
        btnRegister= findViewById(R.id.imgBtn_Register);
    }
}
