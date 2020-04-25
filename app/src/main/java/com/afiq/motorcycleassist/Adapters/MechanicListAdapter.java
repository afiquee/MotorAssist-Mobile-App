package com.afiq.motorcycleassist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.Mechanic;
import com.afiq.motorcycleassist.R;

import java.util.List;

public class MechanicListAdapter extends RecyclerView.Adapter<MechanicListAdapter.ViewHolder> {
    private List<Mechanic> itemList;
    private Context context;
    private MechanicListAdapter.OnListListener mOnListListener;

    public MechanicListAdapter(List<Mechanic> itemList, MechanicListAdapter.OnListListener onListListener){
        this.itemList = itemList;
        this.mOnListListener = onListListener;
    }
    @NonNull
    @Override
    public MechanicListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanic_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MechanicListAdapter.ViewHolder slv = new MechanicListAdapter.ViewHolder(layoutView, mOnListListener);
        return slv;
    }

    @Override
    public void onBindViewHolder(MechanicListAdapter.ViewHolder holder, int position) {
        holder.head.setText(itemList.get(position).getMechName());
        holder.desc.setText(itemList.get(position).getMechEmail());
        holder.right.setText(itemList.get(position).getMechStatus());



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView head,desc,right;
        RelativeLayout parentLayout;
        MechanicListAdapter.OnListListener onListListener;
        public ViewHolder(@NonNull View itemView, MechanicListAdapter.OnListListener onListListener) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.textViewHead);
            desc = (TextView) itemView.findViewById(R.id.textViewDesc);
            right = (TextView) itemView.findViewById(R.id.textViewRight);
            //parentLayout = itemView.findViewById(R.id.parentLayout);
            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onListListener.onListClick(getAdapterPosition());

        }
    }
    public interface OnListListener {
        void onListClick (int position);

    }
}
