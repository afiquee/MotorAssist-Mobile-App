package com.afiq.motorcycleassist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.GoodSamaritanRequest;
import com.afiq.motorcycleassist.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GSRHistoryAdapter extends RecyclerView.Adapter<GSRHistoryAdapter.ViewHolder> {
    private List<GoodSamaritanRequest> itemList;
    private Context context;
    private GSRHistoryAdapter.OnListListener mOnListListener;

    public GSRHistoryAdapter(List<GoodSamaritanRequest> itemList, GSRHistoryAdapter.OnListListener onListListener){
        this.itemList = itemList;
        this.mOnListListener = onListListener;
    }
    @NonNull
    @Override
    public GSRHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_motorcyclist_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        GSRHistoryAdapter.ViewHolder slv = new GSRHistoryAdapter.ViewHolder(layoutView, mOnListListener);
        return slv;
    }

    @Override
    public void onBindViewHolder(GSRHistoryAdapter.ViewHolder holder, int position) {

        holder.head.setText(itemList.get(position).getGsrName());
        holder.desc.setText(itemList.get(position).getGsrDistance()+ "km");
        try {
            Date startTime=new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(itemList.get(position).getGsrStartTime());
            Date endTime=new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(itemList.get(position).getGsrEndTime());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(startTime);
            holder.date.setText(strDate);
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String startHour = timeFormat.format(startTime);
            String endHour = timeFormat.format(endTime);
            holder.time.setText(startHour + " - "+endHour);
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView head,desc,date,time;
        GSRHistoryAdapter.OnListListener onListListener;
        public ViewHolder(@NonNull View itemView, GSRHistoryAdapter.OnListListener onListListener) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.textViewHead);
            desc = (TextView) itemView.findViewById(R.id.textViewDesc);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
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
