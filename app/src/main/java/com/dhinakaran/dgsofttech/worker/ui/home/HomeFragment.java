package com.dhinakaran.dgsofttech.worker.ui.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dhinakaran.dgsofttech.worker.CategoryAdapter;
import com.dhinakaran.dgsofttech.worker.CategoryModel;
import com.dhinakaran.dgsofttech.worker.DashboardActivity;
import com.dhinakaran.dgsofttech.worker.HomeModel;
import com.dhinakaran.dgsofttech.worker.HomePageAdapter;
import com.dhinakaran.dgsofttech.worker.HorizontalProductScrollModel;
import com.dhinakaran.dgsofttech.worker.ProductDetailsActivity;
import com.dhinakaran.dgsofttech.worker.R;
import com.dhinakaran.dgsofttech.worker.SliderModel;
import com.dhinakaran.dgsofttech.worker.dbQuaries;
import com.dhinakaran.dgsofttech.worker.wishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.dhinakaran.dgsofttech.worker.dbQuaries.categoryModelList;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.clearData;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.lists;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadCategories;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadFragmentData;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadedCategoriesNames;


public class HomeFragment extends Fragment {


    public static SwipeRefreshLayout swipeRefreshLayout;
    private TextView logout;
    private HomeViewModel homeViewModel;
    public static RecyclerView homerecyclerView;
    public static RecyclerView recyclerViewforCategory;
    private static CategoryAdapter categoryAdapter;
    private ImageView noNet;
    List<String> id = new ArrayList<>();
    private TextView noNetTxt;
    private ConnectivityManager manager;
    private List<CategoryModel> categoryModelListfake = new ArrayList<>();
    private NetworkInfo info;
    private List<HomeModel> homeModelListfake = new ArrayList<>();
    private Button retryBtn;


    public static HomePageAdapter homePageAdapter;

    List<String> rc = new ArrayList<>();
    static List<String> ids = new ArrayList<>();

    static List<HorizontalProductScrollModel> rcList = new ArrayList<>();
    static List<wishlistModel> allp = new ArrayList<>();


    //////GridLayout


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);


        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewforCategory = view.findViewById(R.id.categories_recyclarView);
        noNet = view.findViewById(R.id.no_internet_img);
        noNetTxt = view.findViewById(R.id.noInternetTxt);
        homerecyclerView = view.findViewById(R.id.HomeRV);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        retryBtn = view.findViewById(R.id.retry);

        swipeRefreshLayout.setColorSchemeColors(view.getContext().getResources().getColor(R.color.colorPrimary)
                , view.getContext().getResources().getColor(R.color.colorPrimary)
                , view.getContext().getResources().getColor(R.color.colorPrimary));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewforCategory.setLayoutManager(layoutManager);

        homerecyclerView = view.findViewById(R.id.HomeRV);
        LinearLayoutManager testManager = new LinearLayoutManager(getContext());
        testManager.setOrientation(RecyclerView.VERTICAL);
        homerecyclerView.setLayoutManager(testManager);

        //HOME FAKE
        List<SliderModel> sliderModelListfake = new ArrayList<>();
        sliderModelListfake.add(new SliderModel("", ""));
        sliderModelListfake.add(new SliderModel("", ""));
        sliderModelListfake.add(new SliderModel("", ""));
        sliderModelListfake.add(new SliderModel("", ""));

        List<HorizontalProductScrollModel> horizontalProductScrollModelListfake = new ArrayList<>();
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("", "", "", "", ""));

        homeModelListfake.add(new HomeModel(0, sliderModelListfake));
        homeModelListfake.add(new HomeModel(1, ""));
        homeModelListfake.add(new HomeModel(2, "", "#ffffff", horizontalProductScrollModelListfake, new ArrayList<wishlistModel>()));
        homeModelListfake.add(new HomeModel(3, "#ffffff", "THI", horizontalProductScrollModelListfake, 0));

        //HOME FAKE

        ///categoryFake
        categoryModelListfake.add(new CategoryModel("null", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        categoryModelListfake.add(new CategoryModel("", ""));
        ///categoryFake

        categoryAdapter = new CategoryAdapter(categoryModelListfake);


        homePageAdapter = new HomePageAdapter(homeModelListfake);

        if (categoryModelList.size() == 0) {
            loadCategories(recyclerViewforCategory, view.getContext());
        } else {
            categoryAdapter = new CategoryAdapter(categoryModelList);
            categoryAdapter.notifyDataSetChanged();
        }
        recyclerViewforCategory.setAdapter(categoryAdapter);
        if (lists.size() == 0) {
            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomeModel>());
            loadFragmentData(homerecyclerView, view.getContext(), 0, "HOMEDEV");
        } else {
            homePageAdapter = new HomePageAdapter(lists.get(0));
            homePageAdapter.notifyDataSetChanged();
        }

        homerecyclerView.setAdapter(homePageAdapter);

        /////////////////Recyclar View

        //Internet Connection
        manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnected() == true) {
            homerecyclerView.setVisibility(View.VISIBLE);
            recyclerViewforCategory.setVisibility(View.VISIBLE);
            noNet.setVisibility(View.GONE);
            noNetTxt.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
        } else {
            homerecyclerView.setVisibility(View.GONE);
            noNet.setVisibility(View.VISIBLE);
            recyclerViewforCategory.setVisibility(View.GONE);
            retryBtn.setVisibility(View.VISIBLE);
            Glide.with(view.getContext()).load(R.drawable.nointernet)
                    .apply(new RequestOptions())
                    .placeholder(R.drawable.nonet)
                    .into(noNet);
            noNetTxt.setVisibility(View.VISIBLE);

            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeRefreshLayout.setRefreshing(true);
                    reloadPage(view.getContext());
                }
            });

        }
        //Internet Connection


        ///swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadPage(view.getContext());

            }
        });
        ///swipeRefreshLayout

        return view;
    }

    private void reloadPage(final Context context) {

        clearData();
        info = manager.getActiveNetworkInfo();
        swipeRefreshLayout.setRefreshing(true);
//        categoryModelList.clear();
//        lists.clear();
//        loadedCategoriesNames.clear();

        if (info != null && info.isConnected() == true) {
            homerecyclerView.setVisibility(View.VISIBLE);
            categoryAdapter = new CategoryAdapter(categoryModelListfake);
            recyclerViewforCategory.setAdapter(categoryAdapter);

            recyclerViewforCategory.setVisibility(View.VISIBLE);
            noNet.setVisibility(View.GONE);
            noNetTxt.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            DashboardActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            homePageAdapter = new HomePageAdapter(homeModelListfake);
            homerecyclerView.setAdapter(homePageAdapter);

            loadCategories(recyclerViewforCategory, getContext());
            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomeModel>());
            loadFragmentData(homerecyclerView, getContext(), 0, "HOMEDEV");


        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            homerecyclerView.setVisibility(View.GONE);
            noNet.setVisibility(View.VISIBLE);
            recyclerViewforCategory.setVisibility(View.GONE);
            retryBtn.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(R.drawable.nointernet)
                    .apply(new RequestOptions())
                    .placeholder(R.drawable.nonet)
                    .into(noNet);
            noNetTxt.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);

            DashboardActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeRefreshLayout.setRefreshing(true);
                    reloadPage(context);
                }
            });
        }

    }


}