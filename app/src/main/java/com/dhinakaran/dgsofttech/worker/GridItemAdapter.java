package com.dhinakaran.dgsofttech.worker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> {

    List<GridModel> gridModelList;
    Context context;

    public GridItemAdapter(List<GridModel> gridModelList, Context context) {
        this.gridModelList = gridModelList;
        this.context = context;
    }

    @NotNull
    @Override
    public GridItemAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item, parent, false);
        return new GridItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GridItemAdapter.ViewHolder holder, int position) {
        String name = gridModelList.get(position).getName();
        String price = gridModelList.get(position).getPrice();
        String img = gridModelList.get(position).getImage();
        String  id = gridModelList.get(position).getId();
        holder.setData(name,price,img,id);
    }

    @Override
    public int getItemCount() {
        return gridModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView gname, gprice;
        ImageView imageView;
        ConstraintLayout grid;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            gname = itemView.findViewById(R.id.HorizontalScrollProductName);
            imageView = itemView.findViewById(R.id.HorizontalProductImage);
            gprice = itemView.findViewById(R.id.HorizontalScrollProductPrize);
            grid = itemView.findViewById(R.id.gridp);
        }

        private void setData(String name, String price, String img, final String id) {
            gname.setText(name);
            gprice.setText(price);
            Glide.with(itemView.getContext()).load(img).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(),ProductDetailsActivity.class);
                    intent.putExtra("product_ID",id);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
