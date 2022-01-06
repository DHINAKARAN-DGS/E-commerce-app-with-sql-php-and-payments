package com.dhinakaran.dgsofttech.worker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.dhinakaran.dgsofttech.worker.dbQuaries.categoryModelList;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.lists;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadedCategoriesNames;


public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView logo, name, email;
    public static DrawerLayout drawer;
    FirebaseAuth firebaseAuth;
    public static Activity mainactivity;
    TextView cartCount;
    MenuItem cartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        Toolbar toolbar = findViewById(R.id.carttoolbar);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(DashboardActivity.this, RegisterActivity.class));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        logo = findViewById(R.id.logoTxtVIEW);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    dbQuaries.uname = task.getResult().get("fullname").toString();
                    dbQuaries.uemail = task.getResult().get("email").toString();
                    email = findViewById(R.id.nav_email);
                    name = findViewById(R.id.nav_fullname);

                    name.setText("Hi "+dbQuaries.uname);
                    email.setText(dbQuaries.uemail);
                } else {
                    Toast.makeText(DashboardActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                drawer.closeDrawer(GravityCompat.START);

                if (id == R.id.nav_home) {
                } else if (id == R.id.nav_cart) {
                    Intent intent = new Intent(DashboardActivity.this, CartActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_orders) {
                    Intent intent = new Intent(DashboardActivity.this, Orders.class);
                    startActivity(intent);
                } else if (id == R.id.nav_wishlist) {
                    Intent intent = new Intent(DashboardActivity.this, wishlist.class);
                    startActivity(intent);
                } else if (id == R.id.nav_account) {
                    Intent intent = new Intent(DashboardActivity.this, MyAccount.class);
                    startActivity(intent);
                }firebaseAuth=FirebaseAuth.getInstance();
                return true;

            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


//
//         Passing each menu ID as a set of Ids because each
//         menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_cart, R.id.nav_slideshow)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        cartItem = menu.findItem(R.id.main_cart);

        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badgeIcon);
        badgeIcon.setImageResource(R.drawable.cart);
        cartCount = cartItem.getActionView().findViewById(R.id.badgeCount);
        if (dbQuaries.cartlist.size() == 0) {
            dbQuaries.loadCartList(DashboardActivity.this, new Dialog(DashboardActivity.this), false, cartCount, new TextView(DashboardActivity.this));
            cartCount.setVisibility(View.INVISIBLE);
        } else {
            cartCount.setVisibility(View.VISIBLE);
            if (dbQuaries.cartlist.size() < 99) {
                cartCount.setText(String.valueOf(dbQuaries.cartlist.size()));
            } else {
                cartCount.setText(("99+"));
            }
        }
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();



        if (id == R.id.main_search) {
            Intent intent =new Intent(DashboardActivity.this,searchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_cart) {
            Intent intent = new Intent(DashboardActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_notifications) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void setfragment(Fragment fragment,int FRAGMENTNO)
//    {
//        CurrentFragment = FRAGMENTNO;
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.commit();
//    }

    private void Cart() {
        invalidateOptionsMenu();
    }
}