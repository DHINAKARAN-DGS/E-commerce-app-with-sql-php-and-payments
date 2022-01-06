package com.dhinakaran.dgsofttech.worker;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {


    public static boolean RUNNING_WISHLIST_QUERY = false;
    public static boolean RUNNING_CART_QUERY = false;
    public static boolean RUNNING_Rating_QUERY = false;
    TextView badgeCount;

    public static boolean fromSearch = false;

    private Button couponReedemBtn;
    private LinearLayout cartBtn;
    private TextView cartTxt;
    public static MenuItem cartItem;

    private boolean in_stock = false;

    //productImage
    private ViewPager productImagesViewPager, productDetailsViewPager;
    private TabLayout viewPagerIndicator, productDetailsTabLayout;

    //Title
    private TextView ProductTitle;
    private TextView avgRatingMini, totalRatingMini;
    private TextView product_price, cutted_price;

    //COD
    private ImageView codIndicatorIM;
    private TextView codIndicatorTxt;
    private DocumentSnapshot documentSnapshot;

    //description
    private ConstraintLayout specLayout, detailsLayout;
    private String descriptionData;
    private String productOtherDetails;
    private int tabPostiton = -1;
    private List<productSpecsModel> productSpecsModelList = new ArrayList<>();
    private TextView productdetailsonly;

    //rewards
    private TextView rewardTitle, rewardBody;

    ///RATING
    public static LinearLayout ratenowLayoutContainer;
    private Button buyNow;
    private TextView totalRatingsBig, totalNoRates;
    private LinearLayout ratingsNumbersCont;
    private LinearLayout progressBarContainer;
    private TextView avgRateBig;
    public static int initaialRating;
    ///Coupon Dialog
    public static TextView couponTitle, CouponBody, CouponVAlidity;
    public static RecyclerView RecylerView;
    private static LinearLayout selected;
    private Dialog loadingdialog;

    FirebaseFirestore firebaseFirestore;
    public static String productID;
    FirebaseAuth firebaseAuth;

    List<String> rtags;

    String name,price,mrp;

    public static FloatingActionButton addtoWL;
    public static Boolean ALREADY_ADDED_TO_WISH_LIST = false;
    public static Boolean ALREADY_ADDED_TO_CART_LIST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProductTitle = findViewById(R.id.productTitle);
        avgRatingMini = findViewById(R.id.rating_miini);
        totalRatingMini = findViewById(R.id.totalRatingMini);
        product_price = findViewById(R.id.productPrice);
        cutted_price = findViewById(R.id.mrp);
        codIndicatorIM = findViewById(R.id.codindicator);
        codIndicatorTxt = findViewById(R.id.codtext);
        rewardTitle = findViewById(R.id.reward_name);
        rewardBody = findViewById(R.id.rewardDisccription);
        specLayout = findViewById(R.id.TABLAYOUT);
        detailsLayout = findViewById(R.id.ProductDetailsContainer);
        productdetailsonly = findViewById(R.id.ProductsDetailsBody);
        totalRatingsBig = findViewById(R.id.totalnoofpeoplerated);
        ratingsNumbersCont = findViewById(R.id.ratingsNumbersCointainer);
        totalNoRates = findViewById(R.id.totalpeoplerated);
        progressBarContainer = findViewById(R.id.rateingCointainer);
        avgRateBig = findViewById(R.id.averageRate);
        cartBtn = findViewById(R.id.add_to_cart_btn);
        cartTxt = findViewById(R.id.cartText);

        badgeCount = (TextView) findViewById(R.id.badgeCount);

        firebaseFirestore = FirebaseFirestore.getInstance();
        productImagesViewPager = findViewById(R.id.productImagesViewPager);
        viewPagerIndicator = findViewById(R.id.ViewpagerIndicator);
        addtoWL = findViewById(R.id.addtoWishListButton);
        buyNow = findViewById(R.id.buy_now_btn);
        productDetailsTabLayout = findViewById(R.id.productDetailsTablayout);
        productDetailsViewPager = findViewById(R.id.productDetailsViewPager);
        ratenowLayoutContainer = findViewById(R.id.rateingCointainer);

        initaialRating = -1;


        loadingdialog = new Dialog(ProductDetailsActivity.this);
        loadingdialog.setContentView(R.layout.loading_prog);
        loadingdialog.setCancelable(false);
        loadingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        loadingdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        final List<String> productImages = new ArrayList<>();

        productID = getIntent().getStringExtra("product_ID");

        String url = "https://etched-stitches.000webhostapp.com/products.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);
                            Toast.makeText(ProductDetailsActivity.this, "Product Name" + object.getString("product_title"), Toast.LENGTH_SHORT).show();
                            name = object.getString("product_title");
                            price = object.getString("product_price");
                            mrp = object.getString("cutted_price");
                            System.out.println("Name da "+name);
                            ProductTitle.setText(name);
                            product_price.setText(price);
                            cutted_price.setText(mrp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", "2");
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        ProductTitle.setText(name);
        product_price.setText(price);
        cutted_price.setText(mrp);

    }

    public static void openRecylerView() {
        if (RecylerView.getVisibility() == View.GONE) {
            RecylerView.setVisibility(View.VISIBLE);
            selected.setVisibility(View.GONE);
        } else {
            RecylerView.setVisibility(View.GONE);
            selected.setVisibility(View.VISIBLE);
        }
    }

//

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.main_search) {
            Intent intent = new Intent(ProductDetailsActivity.this, searchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_cart) {
            Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();

    }


}