package com.patrykkosieradzki.qrcodereader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<QRCode> mDataSet;

    public CustomAdapter() {
        mDataSet = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            addQRCode(new QRCode(String.valueOf(i)));
        }
    }

    public void addQRCode(QRCode qrCode) {
        mDataSet.add(qrCode);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mDataSet.get(position).getText());
        holder.textView.setOnClickListener(v -> {
            // on click
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(v1 -> {

            });

            textView =  v.findViewById(R.id.text);
        }

        public TextView getTextView() {
            return textView;
        }
    }

}
