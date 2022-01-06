package com.dhinakaran.dgsofttech.worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dhinakaran.dgsofttech.worker.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class delivery extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "";
    private RecyclerView deliveryRV;
    FirebaseFirestore firebaseFirestore;
    private TextView newADDBtn;
    Button continueBtn;
    public static final int SELECT_ADDRESS = 0;

    public static Dialog loadingDialog, paymentDialog;
    ImageButton razorpay;
    ImageButton cashOD;
    public static Dialog loadingdialog;
    private String paymentMethod = "RAZORPAY";

    private boolean successesResponse = false;
    public static boolean fromCart;
    final int UPI_PAYMENT = 0;
    String order_ID = UUID.randomUUID().toString().substring(0, 22);


    public static boolean getQtyIDs = true;
    public static CartAdapter cartAdapter;



    public static List<CartItemsModel> cartItemsModelList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setTitle("Delivery Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;

        continueBtn = findViewById(R.id.cart_continue_btn);


        loadingDialog = new Dialog(delivery.this);
        loadingDialog.setContentView(R.layout.loading_prog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        paymentDialog = new Dialog(delivery.this);
        paymentDialog.setContentView(R.layout.payments_options);
        paymentDialog.setCancelable(true);
        paymentDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        paymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        razorpay = paymentDialog.findViewById(R.id.paytmtype);
        cashOD = paymentDialog.findViewById(R.id.codtype);

//        for (final CartItemsModel cartItemsModel : cartItemsModelList) {
//            String tp = String.valueOf(cartItemsModel.getTotalAmount());
//            totalAmount.setText(tp);
//
//        }




        razorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "RAZORPAY";
                placeOrder();
            }
        });
        cashOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                placeOrder();
            }
        });


        deliveryRV = findViewById(R.id.deliveryRecyclarView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        deliveryRV.setLayoutManager(layoutManager);


        cartAdapter = new CartAdapter(cartItemsModelList, new TextView(delivery.this), false,true,true);
        deliveryRV.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        loadingdialog = new Dialog(delivery.this);
        loadingdialog.setContentView(R.layout.loading_prog);
        loadingdialog.setCancelable(false);
        loadingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        loadingdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "DAAT STUDIOS");
            options.put("description", "ORDERID : " + order_ID);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            String payment = CartAdapter.totalAmount.getText().toString().substring(3, CartAdapter.totalAmount.getText().length() - 2);
            // amount is in paise so please multiple it by 100
            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
            double total = Double.parseDouble(payment);
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            preFill.put("contact", "");

            options.put("prefill", preFill);

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void conformationActivity() {
        getQtyIDs = false;

        for (int x = 0; x < cartItemsModelList.size() - 1; x++) {

            for (String quantityID : cartItemsModelList.get(x).getQuantityIds()) {
                firebaseFirestore.collection("PRODUCTS")
                        .document(cartItemsModelList.get(x).getProductID())
                        .collection("QUANTITY")
                        .document(quantityID).update("user_ID", FirebaseAuth.getInstance().getUid());
                cartItemsModelList.get(x).getQuantityIds().remove(quantityID);

            }


        }
        if (fromCart) {
            loadingdialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListsize = 0;
            final List<Integer> indexList = new ArrayList<>();

            for (int x = 0; x < dbQuaries.cartlist.size(); x++) {
                if (!delivery.cartItemsModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListsize, delivery.cartItemsModelList.get(x).getProductID());
                    cartListsize++;
                } else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", (long) cartListsize);

            FirebaseFirestore.getInstance().collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("USERDATA")
                    .document("MY_CART")
                    .set(updateCartList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 1; x < indexList.size(); x++) {
                                    dbQuaries.cartlist.remove(indexList.get(x).intValue());
                                    dbQuaries.cartItemsModelList.remove(indexList.get(x).intValue());
                                    dbQuaries.cartItemsModelList.remove(dbQuaries.cartItemsModelList.size() - 1);
                                    CartActivity.cartAdapter.notifyDataSetChanged();
                                    HomeFragment.swipeRefreshLayout.setRefreshing(true);
                                }
                                Intent intent = new Intent(delivery.this, orderConformed.class);
                                intent.putExtra("order_ID", order_ID);
                                startActivity(intent);
                            } else {
                                Toast.makeText(delivery.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                            loadingdialog.dismiss();
                        }
                    });

        } else {
            Intent intent = new Intent(delivery.this, orderConformed.class);
            intent.putExtra("order_ID", order_ID);
            startActivity(intent);
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        // payment successful pay_DGU19rDsInjcF2
        Toast.makeText(this, "Transaction success! ", Toast.LENGTH_SHORT).show();
        successesResponse = true;
        loadingDialog.show();
        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("payment_status", "Paid");
        updateStatus.put("order_status", "Ordered");

        firebaseFirestore.collection("ORDERS")
                .document(order_ID).update(updateStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> userOrder = new HashMap<>();
                    userOrder.put("orderID", order_ID);
                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_ID).set(userOrder)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        conformationActivity();
                                    } else {
                                        Toast.makeText(delivery.this, "Failed to update user order!!", Toast.LENGTH_SHORT).show();
                                    }
                                    loadingdialog.dismiss();
                                }
                            });
                } else {
                    Toast.makeText(delivery.this, "Order Canceled", Toast.LENGTH_LONG).show();
                }
                loadingdialog.dismiss();
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e(TAG, "error code " + String.valueOf(i) + " -- Payment failed " + s.toString());
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //quantity

        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemsModelList.size() - 1; x++) {

                for (int y = 0; y < cartItemsModelList.get(x).getProduct_quantity(); y++) {
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                    Map<String, Object> timeStamp = new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemsModelList.get(x).getProductID()).collection("QUANTITY")
                            .document(quantityDocumentName)
                            .set(timeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                cartItemsModelList.get(finalX).getQuantityIds().add(quantityDocumentName);
                                if (finalY + 1 == cartItemsModelList.get(finalX).getProduct_quantity()) {
                                    firebaseFirestore.collection("PRODUCTS").document(cartItemsModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemsModelList.get(finalX).getStockQuantity()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        List<String> serverQuantitiy = new ArrayList<>();

                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                            serverQuantitiy.add(queryDocumentSnapshot.getId());
                                                        }
                                                        long available = 0;
                                                        boolean noLongerAvailable = true;
                                                        for (String qtyID : cartItemsModelList.get(finalX).getQuantityIds()) {
                                                            if (!serverQuantitiy.contains(qtyID)) {
                                                                cartItemsModelList.get(finalX).setQtrError(false);
                                                                if (noLongerAvailable) {
                                                                    cartItemsModelList.get(finalX).setInStock(false);
                                                                } else {
                                                                    cartItemsModelList.get(finalX).setQtrError(true);
                                                                    cartItemsModelList.get(finalX).setMaxQuantity(available);
                                                                    Toast.makeText(delivery.this, "Sorry! all products may not be available try changing the quantities", Toast.LENGTH_SHORT).show();
                                                                }

                                                            } else {
                                                                available++;
                                                                noLongerAvailable = false;
                                                            }
                                                        }
                                                        cartAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(delivery.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                }
                                loadingDialog.dismiss();
                            } else {
                                loadingdialog.dismiss();
                                Toast.makeText(delivery.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    {


                    }

                }

            }
        } else {
            getQtyIDs = true;
        }
        ///quantity



    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {

            for (int x = 0; x < cartItemsModelList.size() - 1; x++) {
                if (!successesResponse) {
                    for (final String quantityID : cartItemsModelList.get(x).getQuantityIds()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS")
                                .document(cartItemsModelList.get(x).getProductID())
                                .collection("QUANTITY")
                                .document(quantityID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (quantityID.equals(cartItemsModelList.get(finalX).getQuantityIds().get(cartItemsModelList.get(finalX).getQuantityIds().size() - 1))) {
                                    cartItemsModelList.get(finalX).getQuantityIds().clear();

                                }
                            }
                        });

                    }
                } else {
                    cartItemsModelList.get(x).getQuantityIds().clear();
                }
            }
        }

    }

    private void placeOrder() {

        loadingDialog.show();
        for (final CartItemsModel cartItemsModel : cartItemsModelList) {
            if (cartItemsModel.getType() == CartItemsModel.CART_ITEM_LAYOUT) {
                final String productID = cartItemsModel.getProductID();

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("order_ID", order_ID);
                orderDetails.put("product_ID", cartItemsModel.getProductID());
                orderDetails.put("user_ID", FirebaseAuth.getInstance().getUid());
                orderDetails.put("product_qty", cartItemsModel.getProduct_quantity());
                if (cartItemsModel.getCuttedPrice() != null) {
                    orderDetails.put("cutted_price", cartItemsModel.getCuttedPrice());
                }
                orderDetails.put("product_price", cartItemsModel.getProduct_price());
                orderDetails.put("payment_method", paymentMethod);
                orderDetails.put("product_image", cartItemsModel.getProduct_image());
                orderDetails.put("product_title", cartItemsModel.getProduct_title());
                orderDetails.put("delivery_price", cartItemsModel.getDeliveryPrice());
                orderDetails.put("city", CartAdapter.pincode.getText().toString());
                orderDetails.put("order_status", "cancelled");
                orderDetails.put("date", FieldValue.serverTimestamp());
                firebaseFirestore.collection("ORDERS")
                        .document(order_ID).collection("ORDER_ITEMS")
                        .document(cartItemsModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {
                            Toast.makeText(delivery.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("total_items", cartItemsModel.getTotalItems());
                orderDetails.put("total_items_price", cartItemsModel.getTotalItemPrice());
                orderDetails.put("delivery_price", cartItemsModel.getDeliveryPrice());
                orderDetails.put("saved_amount", cartItemsModel.getSavedAmount());
                orderDetails.put("total_amount", cartItemsModel.getTotalAmount());
                orderDetails.put("payment_status", "not paid");
                orderDetails.put("order_status", "cancelled");
                orderDetails.put("date", FieldValue.serverTimestamp());
                orderDetails.put("payment_method", paymentMethod);
                orderDetails.put("addresses", CartAdapter.fullAddresses.getText().toString());
                orderDetails.put("fullname", CartAdapter.fullName.getText().toString());
                orderDetails.put("pincode", CartAdapter.pincode.getText().toString());


                firebaseFirestore.collection("ORDERS")
                        .document(order_ID)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (paymentMethod.equals("RAZORPAY")) {
                                razorpay();
                            } else {
                                cod();
                            }
                        } else {
                            Toast.makeText(delivery.this, "Error:", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }


    }

    private void razorpay() {
        getQtyIDs = false;
        paymentDialog.dismiss();
        startPayment();
    }

    private void cod() {
        getQtyIDs = false;
        paymentDialog.dismiss();
        successesResponse = true;
        loadingDialog.show();

        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("payment_status", "COD");
        updateStatus.put("order_status", "Ordered");

        firebaseFirestore.collection("ORDERS")
                .document(order_ID).update(updateStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> userOrder = new HashMap<>();
                    userOrder.put("orderID", order_ID);
                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_ID).set(userOrder)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        conformationActivity();
                                    } else {
                                        Toast.makeText(delivery.this, "Failed to update user order!!", Toast.LENGTH_SHORT).show();
                                    }
                                    loadingdialog.dismiss();
                                }
                            });
                } else {
                    Toast.makeText(delivery.this, "Order Canceled", Toast.LENGTH_LONG).show();
                }
                loadingdialog.dismiss();
            }
        });
    }
}