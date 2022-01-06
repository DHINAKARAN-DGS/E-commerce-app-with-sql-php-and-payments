package com.dhinakaran.dgsofttech.worker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhinakaran.dgsofttech.worker.ui.home.HomeFragment;

public class orderConformed extends AppCompatActivity {

    private ImageView continueBtn;
    private TextView conformationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_conformed);

        continueBtn=findViewById(R.id.continueShopping);
        conformationID = findViewById(R.id.deliveryOrderID);

        String order_ID = getIntent().getStringExtra("order_ID");
        conformationID.setText("ORDER ID: "+order_ID);
        getSupportActionBar().hide();
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(orderConformed.this,DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                HomeFragment.swipeRefreshLayout.setRefreshing(true);
                dbQuaries.cartlist.clear();
                dbQuaries.cartItemsModelList.clear();
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(orderConformed.this,DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}