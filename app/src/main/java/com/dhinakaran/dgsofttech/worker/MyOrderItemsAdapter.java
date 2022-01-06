package com.dhinakaran.dgsofttech.worker;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderItemsAdapter extends RecyclerView.Adapter<MyOrderItemsAdapter.ViewHolder> {

    private List<MyOrderItemsModel> myOrderItemsModelList;
    private Dialog loadingdialog;
    private SimpleDateFormat simpleDateFormat;

    public MyOrderItemsAdapter(List<MyOrderItemsModel> myOrderItemsModelList,Dialog loadingdialog) {
        this.loadingdialog=loadingdialog;
        this.myOrderItemsModelList = myOrderItemsModelList;
    }

    @NonNull
    @Override
    public MyOrderItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderItemsAdapter.ViewHolder holder, int position) {
        String resource = myOrderItemsModelList.get(position).getProduct_image_orders();
        int rating = myOrderItemsModelList.get(position).getRating();
        String productName = myOrderItemsModelList.get(position).getProductTitleOrder();
        String deliveryStatus = myOrderItemsModelList.get(position).getDeliveryStatus();
        Date date = myOrderItemsModelList.get(position).getDate();
        String city = myOrderItemsModelList.get(position).getCity();
        String price = myOrderItemsModelList.get(position).getProductPrice();
        Long qty = myOrderItemsModelList.get(position).getProductQty();
        String delivery = myOrderItemsModelList.get(position).getDeliveryPrice();
        String productId = myOrderItemsModelList.get(position).getProductID();
        String payment = myOrderItemsModelList.get(position).getPaymentMethod();
        String orderID = myOrderItemsModelList.get(position).getOrderID();
        holder.setData(resource, productName, deliveryStatus, rating, date, city, price, qty, delivery, productId, position, payment,orderID);
    }

    @Override
    public int getItemCount() {
        return myOrderItemsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productTitleOrder, deliveryStatus, citytxt, pricetxt, qtytxt, totalAmount, paymentTxt,orderTxt;
        private ImageView productImageOrders, deliveryIndicator;
        private LinearLayout ratenowLayout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productTitleOrder = itemView.findViewById(R.id.ProductTitleOrders);
            productImageOrders = itemView.findViewById(R.id.product_images_orders);
            deliveryStatus = itemView.findViewById(R.id.orderDeliveredDate);
            ratenowLayout = itemView.findViewById(R.id.orderrateingContainer);
            citytxt = itemView.findViewById(R.id.deliveryCity);
            pricetxt = itemView.findViewById(R.id.deliveryprice);
            qtytxt = itemView.findViewById(R.id.deliveryQtyTextView);
            totalAmount = itemView.findViewById(R.id.totalAmountOrder);
            paymentTxt = itemView.findViewById(R.id.paymentsTxt);
            orderTxt = itemView.findViewById(R.id.orderIDText);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }

        private void setData(String resource, String ordersTitle, String deliverySTATUS, final int rating, Date date, String city, String price, Long qty, String delivery, final String productID, final int position, String payment,String orderID) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(productImageOrders);
            productTitleOrder.setText(ordersTitle);
            simpleDateFormat = new SimpleDateFormat("EEE-dd-MMM-YYYY-hh-mm-aa");
            deliveryStatus.setText(deliverySTATUS + " (" + simpleDateFormat.format(date) + ")");
            pricetxt.setText("Rs." + price + "/-");
            citytxt.setText("Shipped to " + city);
            qtytxt.setText("Qty: " + qty);
            orderTxt.setText("order ID - "+orderID);
            int totalPrice = Integer.parseInt(price) * Integer.parseInt(String.valueOf(qty));

            if (totalPrice >= 500) {
                totalAmount.setText("Total amount Rs." + totalPrice + "/-");
            } else {
                totalPrice = totalPrice + 40;
                totalAmount.setText("Total amount Rs." + totalPrice  + "/-*");
            }


            paymentTxt.setText("Payment mode: " + payment);
            /////rateing

            setRating(rating);
            for (int x = 0; x < ratenowLayout.getChildCount(); x++) {

                final int starposition = x;
                ratenowLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingdialog.show();
                        setRating(starposition);
                        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS")
                                .document(productID);
                        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                            @Nullable
                            @Override
                            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(documentReference);

                                System.out.println("in");
                                Long increase = (snapshot.getLong((starposition + 1) + "_star")) + 1;
                                System.out.println("dec");
                                Long decrease = snapshot.getLong(starposition + 1 + "_star") - 1;
                                System.out.println("0");
                                if (rating != 0) {
                                    System.out.println("1");
                                    transaction.update(documentReference, starposition + 1 + "_star", increase);
                                    System.out.println("2");
                                    transaction.update(documentReference, rating + 1 + "_star", decrease);
                                } else {
                                    transaction.update(documentReference, starposition + 1 + "_star", increase);
                                }

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Object>() {
                            @Override
                            public void onSuccess(Object object) {

                                Map<String, Object> myrating = new HashMap<>();

                                if (dbQuaries.myRatedIDs.contains(productID)) {
                                    myrating.put("rating_" + dbQuaries.myRatedIDs.indexOf(productID), (long) starposition + 1);
                                } else {
                                    myrating.put("list_size", (long) dbQuaries.myRatedIDs.size() + 1);
                                    myrating.put("product_ID_" + dbQuaries.myRatedIDs.size(), productID);
                                    myrating.put("rating_" + dbQuaries.myRatedIDs.size(), (long) starposition + 1);

                                }
                                FirebaseFirestore.getInstance().collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USERDATA")
                                        .document("MY_RATINGS")
                                        .update(myrating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dbQuaries.orderItemsModelList.get(position).setRating(starposition);
                                            if (dbQuaries.myRatedIDs.contains(productID)) {
                                                System.out.println("1");
                                                dbQuaries.myRating.set(dbQuaries.myRatedIDs.indexOf(productID), Long.parseLong(String.valueOf(starposition + 1)));
                                            } else {
                                                dbQuaries.myRatedIDs.add(productID);
                                                System.out.println("2");
                                                dbQuaries.myRating.add(Long.parseLong(String.valueOf(starposition + 1)));
                                            }
                                        } else {
                                            Toast.makeText(itemView.getContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }loadingdialog.dismiss();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingdialog.dismiss();
                                Toast.makeText(itemView.getContext(), "Error:"+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingdialog = new Dialog(itemView.getContext());
                    loadingdialog.setContentView(R.layout.thank_you_layout);
                    loadingdialog.setCancelable(true);
                    loadingdialog.getWindow().setBackgroundDrawable(itemView.getContext().getDrawable(R.drawable.slider_bg));
                    loadingdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    loadingdialog.show();
                    TextView contactUS = loadingdialog.findViewById(R.id.ContactUS);
                    contactUS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(itemView.getContext(),contactUsActivity.class);
                            itemView.getContext().startActivity(intent);
                        }
                    });

                }
            });

            /////rateing
        }

        private void setRating(int starposition) {
            for (int x = 0; x < ratenowLayout.getChildCount(); x++) {
                ImageView starbtn = (ImageView) ratenowLayout.getChildAt(x);
                starbtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#887D7D")));
                if (x <= starposition) {
                    starbtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F5B402")));
                }
            }
        }
    }
}