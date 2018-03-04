package com.patrykkosieradzki.qrcodereader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mQrImage;
        private TextView mTitle;
        private TextView mDataType;

        private ViewHolder(View view) {
            super(view);

            mQrImage = view.findViewById(R.id.qrImage);
            mTitle =  view.findViewById(R.id.title);
            mDataType = view.findViewById(R.id.dataType);
        }

    }

    private ArrayList<QRCode> mDataSet;

    public CustomAdapter() {
        mDataSet = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            mDataSet.add(new QRCode("1", "2", "3"));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mDataSet.get(position).title);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }





}
