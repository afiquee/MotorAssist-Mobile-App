package com.afiq.motorcycleassist.ShopListRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.R;

public class ShopListViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView shopId;
    public ShopListViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        shopId = (TextView) itemView.findViewById(R.id.shopId);
    }

    @Override
    public void onClick(View v) {

    }
}
