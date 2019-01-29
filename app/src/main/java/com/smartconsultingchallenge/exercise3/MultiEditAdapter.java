package com.smartconsultingchallenge.exercise3;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smartconsultingchallenge.R;

public class MultiEditAdapter extends RecyclerView.Adapter<MultiEditAdapter.MultiEditViewHolder> {

    private final int ITEM_COUNT = 50;

    private int mLayouts[] = {
            R.layout.list_item_exercise3_regular,
            R.layout.list_item_exercise3_numbers,
            R.layout.list_item_exercise3_uppercase
    };

    @NonNull
    @Override
    public MultiEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(mLayouts[viewType], parent, false);
        return new MultiEditViewHolder(view, mLayouts[viewType] == R.layout.list_item_exercise3_uppercase);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiEditViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position % mLayouts.length;
    }

    class MultiEditViewHolder extends RecyclerView.ViewHolder {
        private EditText editText;

        public MultiEditViewHolder(@NonNull View itemView, boolean isUpperCase) {
            super(itemView);
            editText = itemView.findViewById(R.id.edit_et);

            if (isUpperCase) {
                editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            }
        }
    }
}
