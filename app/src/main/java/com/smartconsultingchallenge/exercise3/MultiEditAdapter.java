package com.smartconsultingchallenge.exercise3;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smartconsultingchallenge.R;

public class MultiEditAdapter extends RecyclerView.Adapter<MultiEditAdapter.MultiEditViewHolder> {

    private final String[] itemValues = new String[50];

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
        holder.editText.setText(itemValues[position]);
    }

    @Override
    public int getItemCount() {
        return itemValues.length;
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
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    itemValues[getAdapterPosition()] = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            if (isUpperCase) {
                editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            }
        }
    }
}
