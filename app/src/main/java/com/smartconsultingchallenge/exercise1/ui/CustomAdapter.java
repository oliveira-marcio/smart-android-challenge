package com.smartconsultingchallenge.exercise1.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartconsultingchallenge.R;

import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_LOCAL_NAME;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_EXT_CODE;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private Cursor mCursor;

    public CustomAdapter() {
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_exercise1, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int postalCodeColumnIndex = mCursor.getColumnIndex(COLUMN_POSTAL_CODE);
        int postalExtCodeColumnIndex = mCursor.getColumnIndex(COLUMN_POSTAL_EXT_CODE);
        int localNameColumnIndex = mCursor.getColumnIndex(COLUMN_LOCAL_NAME);

        holder.postalView.setText(String.format("%s-%s, %s",
                mCursor.getString(postalCodeColumnIndex),
                mCursor.getString(postalExtCodeColumnIndex),
                mCursor.getString(localNameColumnIndex)
        ));
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView postalView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            postalView = itemView.findViewById(R.id.postal_view);
        }
    }
}