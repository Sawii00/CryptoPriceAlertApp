package com.example.cryptotracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PriceAlertAdapter extends RecyclerView.Adapter {

    public List<PriceAlert> list = new ArrayList<>();
    public PriceAlertAdapter()
    {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_element, parent, false);

        return new PriceAlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ((PriceAlertViewHolder)holder).getTicker().setText(list.get(position).pair_ticker);
        ((PriceAlertViewHolder)holder).getPrice().setText(((Double)list.get(position).price).toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PriceAlertViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView ticker;
        private final TextView price;
        public PriceAlertViewHolder(View itemView) {
            super(itemView);
            ticker = (TextView)itemView.findViewById(R.id.list_element_ticker);
            price = (TextView)itemView.findViewById(R.id.list_element_price);
        }

        public TextView getTicker() {
            return ticker;
        }

        public TextView getPrice() {
            return price;
        }
    }

}
