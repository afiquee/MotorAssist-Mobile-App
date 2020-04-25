package com.afiq.motorcycleassist.MotorcycleListRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.R;

import java.util.List;

public class MotorcycleListAdapter extends RecyclerView.Adapter<MotorcycleListAdapter.ViewHolder> {
    private List<Motorcycle> itemList;
    private Context context;
    private OnListListener mOnListListener;

    public MotorcycleListAdapter(List<Motorcycle> itemList, OnListListener onListListener){
        this.itemList = itemList;
        this.mOnListListener = onListListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_motorcycle_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ViewHolder slv = new ViewHolder(layoutView, mOnListListener);
        return slv;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        holder.motorId.setText(itemList.get(position).getMotorId());
        holder.motorDesc.setText(itemList.get(position).getMotorBrand()+" "+itemList.get(position).getmotorModel());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView motorId,motorDesc;
        public LinearLayout itemLayout;
        CardView parentLayout;
        public ImageView edit,delete;
        OnListListener onListListener;
        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            itemLayout = (LinearLayout)itemView.findViewById(R.id.itemLayout);
            motorId = (TextView) itemView.findViewById(R.id.textViewHead);
            motorDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            edit = (ImageView)itemView.findViewById(R.id.edit);
            delete = (ImageView)itemView.findViewById(R.id.delete);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            this.onListListener = onListListener;
            parentLayout.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if(v == edit){
                onListListener.onEditClick(getAdapterPosition());
            }
            else if(v == delete){
                onListListener.onDeleteClick(getAdapterPosition());
            }
            else if(v == parentLayout){
                onListListener.onListClick(getAdapterPosition());
            }



        }
    }
    public interface OnListListener {
        void onListClick (int position);
        void onEditClick (int position);
        void onDeleteClick (int position);

    }

}
