package com.dhinakaran.dgsofttech.worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static com.dhinakaran.dgsofttech.worker.dbQuaries.lists;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadCategories;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadFragmentData;
import static com.dhinakaran.dgsofttech.worker.dbQuaries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity
{
    private RecyclerView categoryRecyclarView;
    HomePageAdapter homePageAdapter;
    private List<HomeModel> homeModelListfake = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        categoryRecyclarView = findViewById(R.id.CategoryRecyclarView);
        List<SliderModel> sliderModelListfake = new ArrayList<>();
        sliderModelListfake.add(new SliderModel("null"," "));
        sliderModelListfake.add(new SliderModel("null"," "));
        sliderModelListfake.add(new SliderModel("null"," "));
        sliderModelListfake.add(new SliderModel("null"," "));

        List<HorizontalProductScrollModel> horizontalProductScrollModelListfake = new ArrayList<>();
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelListfake.add(new HorizontalProductScrollModel("","","","",""));

        homeModelListfake.add(new HomeModel(0,sliderModelListfake));
        homeModelListfake.add(new HomeModel(1,""));
        homeModelListfake.add(new HomeModel(2,"","#ffffff",horizontalProductScrollModelListfake,new ArrayList<wishlistModel>()));
//        homeModelListfake.add(new HomeModel(3,"#ffffff","",horizontalProductScrollModelListfake,0));


        //VIEW PAGER
        /////////////////Recyclar View
        LinearLayoutManager testManager = new LinearLayoutManager(this);
        testManager.setOrientation(RecyclerView.VERTICAL);
        categoryRecyclarView.setLayoutManager(testManager);

        homePageAdapter = new HomePageAdapter(homeModelListfake);


        int listPostiton =0;

        for (int x=0;x<loadedCategoriesNames.size() ;x++)
        {
            if (loadedCategoriesNames.get(x).equals(title.toUpperCase()))
            {
                listPostiton = x;

            }
        }
        if (listPostiton == 0){
            loadedCategoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomeModel>());
            loadFragmentData(categoryRecyclarView,this,loadedCategoriesNames.size()-1,title+"DEV");
        }else {
            homePageAdapter = new HomePageAdapter(lists.get(listPostiton));
        }

        categoryRecyclarView.setAdapter(homePageAdapter);
        homePageAdapter.notifyDataSetChanged();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        int id =item.getItemId();

        if (id==R.id.main_search)
        {
            Intent intent =new Intent(CategoryActivity.this,searchActivity.class);
            startActivity(intent);
            return true;
        }
        else  if (id== android.R.id.home)
        {
            finish();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
}