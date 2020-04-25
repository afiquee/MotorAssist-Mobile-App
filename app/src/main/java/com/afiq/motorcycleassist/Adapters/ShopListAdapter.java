package com.afiq.motorcycleassist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.Shop;
import com.afiq.motorcycleassist.R;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {
    private List<Shop> itemList;
    private Context context;
    private ShopListAdapter.OnListListener mOnListListener;

    public ShopListAdapter(List<Shop> itemList, ShopListAdapter.OnListListener onListListener){
        this.itemList = itemList;
        this.mOnListListener = onListListener;
    }
    @NonNull
    @Override
    public ShopListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ShopListAdapter.ViewHolder slv = new ShopListAdapter.ViewHolder(layoutView, mOnListListener);
        return slv;
    }

    @Override
    public void onBindViewHolder(ShopListAdapter.ViewHolder holder, int position) {
        holder.head.setText(itemList.get(position).getShopName());
        holder.desc.setText(itemList.get(position).getShopEmail());
        holder.right.setText(itemList.get(position).getShopStatus());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView head,desc,right;
        RelativeLayout parentLayout;
        ShopListAdapter.OnListListener onListListener;
        public ViewHolder(@NonNull View itemView, ShopListAdapter.OnListListener onListListener) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.textViewHead);
            desc = (TextView) itemView.findViewById(R.id.textViewDesc);
            right = (TextView) itemView.findViewById(R.id.textViewRight);
            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onListListener.onListClick(getAdapterPosition());

        }
    }
    public interface OnListListener {
        void onListClick(int position);

    }
}
