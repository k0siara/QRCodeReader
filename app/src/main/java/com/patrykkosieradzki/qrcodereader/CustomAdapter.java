package com.patrykkosieradzki.qrcodereader;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.patrykkosieradzki.qrcodereader.model.QRCode;

public class CustomAdapter extends FirebaseRecyclerAdapter<QRCode, CustomAdapter.ViewHolder> {
    
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CustomAdapter(@NonNull FirebaseRecyclerOptions<QRCode> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull QRCode model) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getRef(position).removeValue();

                return true;
            }
        });

        holder.mText.setText(model.text);
        holder.mDataType.setText(model.type);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mQrImage;
        public TextView mText;
        public TextView mDataType;

        public ViewHolder(View view) {
            super(view);

            mQrImage = view.findViewById(R.id.qrImage);
            mText =  view.findViewById(R.id.title);
            mDataType = view.findViewById(R.id.dataType);
        }

    }

}
