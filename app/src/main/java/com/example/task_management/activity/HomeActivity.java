package com.example.task_management.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.task_management.R;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.adapter.ViewPagerAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.PaginationTask;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity{
    BottomNavigationView navigationView;
    ViewPager viewPager;
    DrawerLayout drawerLayout;
    NavigationView headerNavigationView;
    ActionBarDrawerToggle drawerToggle;
    boolean isDrawerOpen =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.footer_new);
        navigationView = findViewById(R.id.btm_footer);
        viewPager = findViewById(R.id.view_pager);
        setUpViewPager();
        drawerLayout = findViewById(R.id.drawer_layout);
        headerNavigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        headerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                    {
                        Toast.makeText(HomeActivity.this, "Bạn đang ở trang chủ", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.nav_profile:
                    {
                        Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_logout:
                    {
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        });
        navigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch (item.getItemId()){
                    case R.id.btm_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.btm_search:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.btm_group:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.btm_notice:
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
        {
            Log.e("123", String.valueOf(drawerLayout.isDrawerOpen(GravityCompat.START)));
            if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.setZ(0);
                isDrawerOpen = false;
            }
            if(isDrawerOpen == false) {
                drawerLayout.setZ(1);
                isDrawerOpen = true;
            }
            else {
                drawerLayout.setZ(0);
                isDrawerOpen = false;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
    private void setUpViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
    }
}
