package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class SignInActivity extends AppCompatActivity {
    EditText editTextEmail;
    EditText editTextPassword;
    ImageButton btnLogin;
    APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sign_in);

        initView();
        if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn())
        {
            Intent intent = new Intent(SignInActivity.this, TaskActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("userObject", stateMessage.getUser());
//                    intent.putExtras(bundle);
            startActivity(intent);
            return;
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testLogin();
            }
        });

    }

    private void testLogin() {
        final String username = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            editTextEmail.setError("Please enter your username");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }
        apiService = RetrofitClient.getInstance().create(APIService.class);

        apiService.getAccessTokenLogin(username, password).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken accessToken = response.body();
                if (accessToken != null) {
                    Toast.makeText(SignInActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, TaskActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("userObject", stateMessage.getUser());
//                    intent.putExtras(bundle);
                    startActivity(intent);
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(accessToken.getAccessToken());
                } else {
                    Toast.makeText(SignInActivity.this, "Wrong Account", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void initView(){
        editTextEmail = findViewById(R.id.edittext_email);
        editTextPassword =  findViewById(R.id.edittext_password);
        btnLogin = findViewById(R.id.imgBtn_Login);
    }
}
