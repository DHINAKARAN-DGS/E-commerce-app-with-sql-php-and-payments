package com.dhinakaran.dgsofttech.worker;

import android.support.v4.app.INotificationSideChannel;

import java.util.ArrayList;
import java.util.List;

public class HomeModel {
    public static final int BannerSlider = 0;
    public static final int STRIP_AD = 1;
    public static final int HORIZONTAL_PRODUCT_LAYOUT = 2;
    public static final int GRID_PRODUCT_LAYOUT = 3;


    private int type;
    private int index;
    private String color;


    ////Banner Slider
    public HomeModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    private List<SliderModel> sliderModelList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;

    }
    //////Banner Slider

    //////////Strip Ad
    private String  resource;
    public HomeModel(int type, String  resource) {
        this.type = type;
        this.resource = resource;
    }
    public String  getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    //////////Strip Ad

    //HORIZONTAL
    private String title;
    private List<HorizontalProductScrollModel>horizontalProductScrollModelList;
    private List<wishlistModel>viewAllProductsList;

    public HomeModel(int type, String title,String color, List<HorizontalProductScrollModel> horizontalProductScrollModelList,List<wishlistModel> viewAllProductsList)
    {
        this.color=color;
        this.type = type;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductsList=viewAllProductsList;
    }



    public List<wishlistModel> getViewAllProductsList() {
        return viewAllProductsList;
    }

    public void setViewAllProductsList(List<wishlistModel> viewAllProductsList) {
        this.viewAllProductsList = viewAllProductsList;
    }
    //HORIZONTAL
    //Grid
    List<HorizontalProductScrollModel> gridModels ;

    public List<HorizontalProductScrollModel> getGridModels() {
        return gridModels;
    }

    public void setGridModels(List<HorizontalProductScrollModel> gridModels) {
        this.gridModels = gridModels;
    }

    public HomeModel(int type, String backColor, String title, List<HorizontalProductScrollModel> gridModels, int index)
    {
        this.color=backColor;
        this.type = type;
        this.title = title;
        this.gridModels = gridModels;
        this.index = index;
    }//Grid

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }


}
