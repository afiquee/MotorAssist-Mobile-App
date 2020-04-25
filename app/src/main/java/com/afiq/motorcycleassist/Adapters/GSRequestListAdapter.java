package com.afiq.motorcycleassist.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
import com.afiq.motorcycleassist.Models.Motorcyclist;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MotorcyclistVM;

import java.util.List;

public class GSRequestListAdapter extends RecyclerView.Adapter<GSRequestListAdapter.ViewHolder> {

    private List<GoodSamaritanRequest> itemList;
    private Context context;
    private GSRequestListAdapter.OnListListener mOnListListener;

    public GSRequestListAdapter(List<GoodSamaritanRequest> itemList, GSRequestListAdapter.OnListListener onListListener) {
        this.itemList = itemList;
        this.mOnListListener = onListListener;
    }

    @NonNull
    @Override
    public GSRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gsrequest_list, null, false);
        context = parent.getContext();
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        GSRequestListAdapter.ViewHolder slv = new GSRequestListAdapter.ViewHolder(layoutView, mOnListListener);
        return slv;
    }

    @Override
    public void onBindViewHolder(GSRequestListAdapter.ViewHolder holder, int position) {
        holder.requestName.setText(itemList.get(position).getGsrName());
        double distance = itemList.get(position).getGsrDistance();
        if(distance > 100){
            holder.desc.setText(itemList.get(position).getGsrDistance()+ " m");
        }
        else{
            holder.desc.setText(itemList.get(position).getGsrDistance()+ " km");
        }

        if(distance == 0){
            holder.desc.setText("");
        }


        MotorcyclistVM motorcyclistVM = ViewModelProviders.of((FragmentActivity) context).get(MotorcyclistVM.class);
        motorcyclistVM.getMotorcyclist(itemList.get(position).getGsrRequesterId()).observe((FragmentActivity) context, motorcyclist -> {
            if(motorcyclist != null){
                Motorcyclist M = (Motorcyclist)motorcyclist;
                holder.right.setText(M.getMotorcyclistName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView requestName,desc,right;
        GSRequestListAdapter.OnListListener onListListener;

        public ViewHolder(@NonNull View itemView, GSRequestListAdapter.OnListListener onListListener) {
            super(itemView);
            requestName = (TextView) itemView.findViewById(R.id.textViewHead);
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
        void onListClick(int position);

    }

}
