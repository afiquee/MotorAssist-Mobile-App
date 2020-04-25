package com.afiq.motorcycleassist.MotorcycleListRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.R;

public class MotorcycleListViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView motorId;
    public MotorcycleListViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        motorId = (TextView) itemView.findViewById(R.id.textViewHead);
    }

    @Override
    public void onClick(View v) {


    }
}
