package com.afiq.motorcycleassist.ShopListRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.R;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListViewHolders> {
    private List<ShopListObject> itemList;
    private Context context;

    public ShopListAdapter(List<ShopListObject> itemList, Context context){
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public ShopListViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ShopListViewHolders slv = new ShopListViewHolders(layoutView);
        return slv;
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListViewHolders holder, int position) {
        holder.shopId.setText(itemList.get(position).getShopId());

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
