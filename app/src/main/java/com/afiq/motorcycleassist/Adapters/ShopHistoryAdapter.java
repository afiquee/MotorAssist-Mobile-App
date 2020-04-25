package com.afiq.motorcycleassist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.Mechanic;
import com.afiq.motorcycleassist.Models.Request;
import com.afiq.motorcycleassist.R;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShopHistoryAdapter extends RecyclerView.Adapter<ShopHistoryAdapter.ViewHolder> implements LifecycleOwner {
    private List<Request> itemList;
    private Context context;
    private ShopHistoryAdapter.OnListListener mOnListListener;

    public ShopHistoryAdapter(List<Request> itemList, ShopHistoryAdapter.OnListListener onListListener){
        this.itemList = itemList;
        this.mOnListListener = onListListener;
    }
    @NonNull
    @Override
    public ShopHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_history, null, false);
        context = parent.getContext();
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ShopHistoryAdapter.ViewHolder slv = new ShopHistoryAdapter.ViewHolder(layoutView, mOnListListener);
        return slv;
    }

    @Override
    public void onBindViewHolder(ShopHistoryAdapter.ViewHolder holder, int position) {
        holder.head.setText(itemList.get(position).getServiceName());

        MechanicVM mechanicVM = ViewModelProviders.of((FragmentActivity) context).get(MechanicVM.class);
        mechanicVM.getMechanic(itemList.get(position).getMechId()).observe((FragmentActivity) context, mechanics -> {
            if(mechanics != null){
                Mechanic M = (Mechanic)mechanics;
                holder.mid.setText(M.getMechName());
            }
        });
        holder.desc.setText("RM "+itemList.get(position).getFinalTotal());
        try {
            Date startTime=new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(itemList.get(position).getStartTime());
            Date endTime=new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(itemList.get(position).getEndTime());
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

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView head,mid,desc,date,time;
        ShopHistoryAdapter.OnListListener onListListener;
        public ViewHolder(@NonNull View itemView, ShopHistoryAdapter.OnListListener onListListener) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.textViewHead);
            mid = (TextView) itemView.findViewById(R.id.textViewMid);
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
        void onListClick (int position);

    }
}
