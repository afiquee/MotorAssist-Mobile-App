package com.afiq.motorcycleassist.MotorcyclistHistoryRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MotorcyclistHistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener  {

    public TextView serviceName;
    public TextView requestDistance;
    public TextView finalTotal;

    public MotorcyclistHistoryViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


    }
}
