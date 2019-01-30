package com.smartconsultingchallenge.exercise2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.smartconsultingchallenge.R;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private final int ITEM_COUNT = 50;
    private int mVerticalOffset;

    public CustomAdapter(int verticalOffset){
        mVerticalOffset = verticalOffset;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_exercise2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.container.getLayoutParams();
        params.setMargins(0, position == 0 ? mVerticalOffset : 0, 0, 0);
        holder.container.setLayoutParams(params);
        holder.textView.setText(String.format("%02d", position + 1));
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private FrameLayout container;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}