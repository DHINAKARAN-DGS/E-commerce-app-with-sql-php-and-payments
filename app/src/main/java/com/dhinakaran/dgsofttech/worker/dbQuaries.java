package com.dhinakaran.dgsofttech.worker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dhinakaran.dgsofttech.worker.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dhinakaran.dgsofttech.worker.ProductDetailsActivity.addtoWL;
import static com.dhinakaran.dgsofttech.worker.ProductDetailsActivity.productID;


public class dbQuaries {
    public static FirebaseFirestore firestore;
    public static String uemail, uname;

    public static boolean done254 = false;

    public static List<CategoryModel> categoryModelList = new ArrayList<CategoryModel>();

    public static List<List<HomeModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishlist = new ArrayList<>();
    public static List<wishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedIDs = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartlist = new ArrayList<>();
    public static List<CartItemsModel> cartItemsModelList = new ArrayList<>();

    public static List<MyOrderItemsModel> orderItemsModelList = new ArrayList<>();

    public static List<addressesModel> addressesModelList = new ArrayList<>();
    public static int selectedAddresses = 0;
    static List<String> ids = new ArrayList<>();
    static List<String> rids = new ArrayList<>();
    static List<String> tags = new ArrayList<>();

    static List<HorizontalProductScrollModel> rcList = new ArrayList<>();
    static List<wishlistModel> allp = new ArrayList<>();

    public static void getProducts(Context context) {
        String url = "https://etched-stitches.000webhostapp.com/getGrid.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            List<HorizontalProductScrollModel> GridProductScrollModelList = new ArrayList<>();
                            for (int x = 0; x < array.length(); x++) {
                                JSONObject object = array.getJSONObject(x);
                                System.out.println("Products" + response);
                                GridProductScrollModelList.add(new
                                        HorizontalProductScrollModel("",
                                        object.getString("PID")
                                        , ""
                                        , ""
                                        , ""
                                ));
                            }
                            lists.get(0).add(new HomeModel(3, "#ffffff", "title", GridProductScrollModelList, 0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error: " + error.toString());
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void getBanners(final Context context) {
        String url = "https://etched-stitches.000webhostapp.com/getBanners.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            List<SliderModel> sliderModellist = new ArrayList<>();
                            for (int x = 0; x < array.length(); x++) {
                                JSONObject object = array.getJSONObject(x);
                                sliderModellist.add(new SliderModel(object.getString("banner"), object.getString("action")));
                                System.out.println(response);
                            }
                            lists.get(0).add(new HomeModel(0, sliderModellist));
                            getProducts(context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void loadCategories(final RecyclerView catRV, final Context context) {
        firestore = FirebaseFirestore.getInstance();

        categoryModelList.clear();
        firestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            catRV.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final RecyclerView homeRV, final Context context, final int index, final String categoryName) {
        firestore = FirebaseFirestore.getInstance();
        getBanners(context);
        firestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase()).collection("TOPDEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            HomePageAdapter adapter = new HomePageAdapter(lists.get(index));
                            homeRV.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(context, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishlist(final Context context, final Dialog dialog, final boolean loadProductsData) {
        firestore = FirebaseFirestore.getInstance();
        wishlist.clear();
        firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid()).collection("USERDATA")
                .document("MY_WISHLIST").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                wishlist.add(task.getResult().get("product_ID_" + x).toString());

                                if (dbQuaries.wishlist.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = true;
                                    if (addtoWL != null) {
                                        addtoWL.setSupportImageTintList(context.getResources().getColorStateList(R.color.red));
                                    }
                                } else {
                                    if (addtoWL != null) {
                                        addtoWL.setSupportImageTintList(context.getResources().getColorStateList(R.color.grey));
                                    }
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = false;
                                }

                                if (loadProductsData) {
                                    wishlistModelList.clear();
                                    final String PRODUCTID = task.getResult().get("product_ID_" + x).toString();
                                    firestore.collection("PRODUCTS")
                                            .document(PRODUCTID)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                                        FirebaseFirestore.getInstance().collection("PRODUCTS")
                                                                .document(PRODUCTID).collection("QUANTITY").get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                                wishlistModelList.add(new
                                                                                        wishlistModel(
                                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                                        0
                                                                                        , (long) documentSnapshot.get("total_ratings")
                                                                                        , documentSnapshot.get("product_title").toString()
                                                                                        , documentSnapshot.get("product_price").toString()
                                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                                        , documentSnapshot.get("average_rating").toString()
                                                                                        , (boolean) documentSnapshot.get("COD")
                                                                                        , PRODUCTID
                                                                                        , true));
                                                                            } else {
                                                                                wishlistModelList.add(new
                                                                                        wishlistModel(
                                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                                        0
                                                                                        , (long) documentSnapshot.get("total_ratings")
                                                                                        , documentSnapshot.get("product_title").toString()
                                                                                        , documentSnapshot.get("product_price").toString()
                                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                                        , documentSnapshot.get("average_rating").toString()
                                                                                        , (boolean) documentSnapshot.get("COD")
                                                                                        , PRODUCTID
                                                                                        , false));
                                                                            }
                                                                            com.dhinakaran.dgsofttech.worker.wishlist.wishlistAdapter.notifyDataSetChanged();
                                                                        } else {
                                                                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });


                                                    } else {
                                                        Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }

                            }
                        } else {
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void removeFromWishList(final int index, final Context context) {
        final String removeProductID = wishlist.get(index);
        wishlist.remove(index);
        Map<String, Object> updateWishList = new HashMap<>();

        for (int x = 0; x < wishlist.size(); x++) {
            updateWishList.put("product_ID_" + x, wishlist.get(x));
        }

        updateWishList.put("list_size", (long) wishlist.size());
        firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USERDATA")
                .document("MY_WISHLIST")
                .set(updateWishList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (wishlistModelList.size() != 0) {
                                wishlistModelList.remove(index);
                                com.dhinakaran.dgsofttech.worker.wishlist.wishlistAdapter.notifyDataSetChanged();
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = false;
                            Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            if (addtoWL != null) {
                                addtoWL.setSupportImageTintList(context.getResources().getColorStateList(R.color.red));
                            }
                            wishlist.add(index, removeProductID);
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.RUNNING_WISHLIST_QUERY = false;

                    }
                });
    }

    public static void loadRatingList(final Context context) {
        if (!ProductDetailsActivity.RUNNING_Rating_QUERY) {
            ProductDetailsActivity.RUNNING_Rating_QUERY = true;
            myRatedIDs.clear();
            myRating.clear();
            firestore.collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("USERDATA")
                    .document("MY_RATINGS").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                List<String> orderProductIds = new ArrayList<>();
                                for (int x = 0; x < orderItemsModelList.size(); x++) {
                                    orderProductIds.add(orderItemsModelList.get(x).getProductID());
                                }

                                for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {

                                    myRatedIDs.add(task.getResult().get("product_ID_" + x).toString());
                                    myRating.add((long) task.getResult().get("rating_" + x));

                                    if (task.getResult().get("product_ID_" + x)
                                            .toString().equals(ProductDetailsActivity.productID)) {
                                        ProductDetailsActivity.initaialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
//                                        if (ProductDetailsActivity.ratenowLayoutContainer != null) {
//                                            ProductDetailsActivity.setRating(initaialRating);
//                                        }
                                    }

                                    if (orderProductIds.contains((task.getResult().get("product_ID_" + x).toString()))) {
                                        orderItemsModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString()))
                                                .setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                                    }

                                }
                                if (Orders.myOrderItemsAdapter != null) {
                                    Orders.myOrderItemsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                            ProductDetailsActivity.RUNNING_Rating_QUERY = false;
                        }
                    });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductsData, final TextView cartCount, final TextView cartTotalAmount) {
        firestore = FirebaseFirestore.getInstance();
        cartlist.clear();
        firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid()).collection("USERDATA")
                .document("MY_CART").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                cartlist.add(task.getResult().get("product_ID_" + x).toString());

                                if (dbQuaries.cartlist.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART_LIST = true;
                                } else {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART_LIST = false;
                                }

                                if (loadProductsData) {
                                    cartItemsModelList.clear();
                                    final String PRODUCTID = task.getResult().get("product_ID_" + x).toString();
                                    firestore.collection("PRODUCTS")
                                            .document(PRODUCTID)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(PRODUCTID)
                                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            int index = 0;
                                                                            if (cartlist.size() >= 2) {
                                                                                index = cartlist.size() - 2;
                                                                            }
                                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                                cartItemsModelList.add(index, new
                                                                                        CartItemsModel(
                                                                                        CartItemsModel.CART_ITEM_LAYOUT,
                                                                                        documentSnapshot.get("product_image_1").toString()
                                                                                        , (long) 0 //offers Applied
                                                                                        , (long) 0
                                                                                        , (long) 1 //quantity
                                                                                        , (long) 0 //coupons applied
                                                                                        , documentSnapshot.get("product_title").toString()
                                                                                        , documentSnapshot.get("product_price").toString()
                                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                                        , PRODUCTID
                                                                                        , true
                                                                                        , (long) documentSnapshot.get("max-quantity")
                                                                                        , (long) documentSnapshot.get("stock_quantity")));
                                                                            } else {
                                                                                cartItemsModelList.add(index, new
                                                                                        CartItemsModel(
                                                                                        CartItemsModel.CART_ITEM_LAYOUT,
                                                                                        documentSnapshot.get("product_image_1").toString()
                                                                                        , (long) 0 //offers Applied
                                                                                        , (long) 0
                                                                                        , (long) 1 //quantity
                                                                                        , (long) 0 //coupons applied
                                                                                        , documentSnapshot.get("product_title").toString()
                                                                                        , documentSnapshot.get("product_price").toString()
                                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                                        , PRODUCTID
                                                                                        , false
                                                                                        , (long) documentSnapshot.get("max-quantity")
                                                                                        , (long) documentSnapshot.get("stock_quantity")));
                                                                            }

                                                                            if (cartlist.size() == 0) {
                                                                                cartItemsModelList.clear();
                                                                            }

                                                                            CartActivity.cartAdapter.notifyDataSetChanged();

                                                                        } else {
                                                                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
//                                    if (cartlist.size() >= 1) {
//                                    LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
//                                    parent.setVisibility(View.VISIBLE);
//                                    }
                                }

                            }
                            if (cartlist.size() >= 1) {
                                cartItemsModelList.add(new CartItemsModel(CartItemsModel.CART_TOTAL_PRICE));
                            }
                            if (cartlist.size() != 0) {
                                cartCount.setVisibility(View.VISIBLE);
                            } else {
                                cartCount.setVisibility(View.INVISIBLE);
                            }
                            if (dbQuaries.cartlist.size() < 99) {
                                cartCount.setText(String.valueOf(dbQuaries.cartlist.size()));
                            } else {
                                cartCount.setText(("99+"));
                            }

                        } else {
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void removeFromCartList(final int index, final Context context, final TextView cartTotalAmount) {
        final String removeProductID = cartlist.get(index);
        cartlist.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();

        for (int x = 0; x < cartlist.size(); x++) {
            updateCartList.put("product_ID_" + x, cartlist.get(x));
        }
        updateCartList.put("list_size", (long) cartlist.size());

        firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USERDATA")
                .document("MY_CART")
                .set(updateCartList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (cartItemsModelList.size() != 0) {
                                cartItemsModelList.remove(index);
                                CartActivity.cartAdapter.notifyDataSetChanged();
                            }
                            if (cartlist.size() == 0) {
                                CartActivity.cartItemsRV.setVisibility(View.GONE);
                            }
                            if (cartlist.size() == 0) {
                                CartActivity.cartAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            cartlist.add(index, removeProductID);
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.RUNNING_CART_QUERY = false;

                    }
                });

    }

    public static void loadAddresses(final Context context, final Dialog loading, final boolean gotoDeliveryActivity) {
        addressesModelList.clear();
        firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid()).collection("USERDATA")
                .document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if ((long) task.getResult().get("list_size") == 0) {
                        if (gotoDeliveryActivity) {
                            Intent intent = new Intent(context, addnewaddress.class);
                            intent.putExtra("INTENT", "deliveryIntent");
                            context.startActivity(intent);
                        } else {

                        }
                    } else {
                        for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                            addressesModelList.add(new
                                    addressesModel(task.getResult().get("fullname_" + x).toString()
                                    , task.getResult().get("mobile_no_" + x).toString()
                                    , task.getResult().get("city_" + x).toString()
                                    , task.getResult().get("locality_" + x).toString()
                                    , task.getResult().get("flatno_" + x).toString()
                                    , task.getResult().get("pincode_" + x).toString()
                                    , task.getResult().get("landmark_" + x).toString()
                                    , task.getResult().get("alter_mobile_no_" + x).toString()
                                    , (boolean) task.getResult().get("selected_" + x)
                            ));
                            if ((boolean) task.getResult().get("selected_" + x)) {
                                selectedAddresses = Integer.parseInt(String.valueOf(x - 1));
                            }
                        }
                        if (gotoDeliveryActivity) {
                            Intent intent = new Intent(context, delivery.class);
                            context.startActivity(intent);
                        }
                    }

                } else {
                    Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();
            }
        });
    }

    public static void loadOrders(final Context context, @Nullable final MyOrderItemsAdapter orderItemsAdapter, final Dialog loadingDialog) {
        orderItemsModelList.clear();
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection("USERS")
                .document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                FirebaseFirestore.getInstance().collection("ORDERS").document(documentSnapshot.getString("orderID")).collection("ORDER_ITEMS").orderBy("date", Query.Direction.DESCENDING).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {

                                                        final MyOrderItemsModel orderItemsmordel = new MyOrderItemsModel(orderItems.getString("product_ID")
                                                                , orderItems.getString("product_image")
                                                                , orderItems.getString("product_title")
                                                                , "Ordered"
                                                                , orderItems.getString("cutted_price")
                                                                , orderItems.getString("order_ID")
                                                                , orderItems.getString("product_price")
                                                                , orderItems.getLong("product_qty")
                                                                , orderItems.getString("payment_method")
                                                                , orderItems.getString("user_ID")
                                                                , orderItems.getDate("date")
                                                                , orderItems.getString("city")
                                                                , orderItems.getString("delivery_price")
                                                        );
                                                        loadingDialog.dismiss();

                                                        orderItemsModelList.add(orderItemsmordel);
                                                    }
                                                    loadRatingList(context);
                                                    if (orderItemsAdapter != null) {
                                                        orderItemsAdapter.notifyDataSetChanged();
                                                    }
                                                    loadingDialog.dismiss();
                                                } else {
                                                    loadingDialog.dismiss();
                                                    Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });
                            }
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishlist.clear();
        wishlistModelList.clear();
        cartlist.clear();
        cartItemsModelList.clear();
        myRatedIDs.clear();
        myRating.clear();
        addressesModelList.clear();
        orderItemsModelList.clear();
        ids.clear();
        rcList.clear();
        allp.clear();
    }


}

