package com.smartconsultingchallenge.exercise1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartconsultingchallenge.R;

public class Exercise1Fragment extends Fragment {

    public Exercise1Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise1, container, false);

        TextView message = view.findViewById(R.id.message);
        message.setText(R.string.title_exercise_1);

        return view;
    }
}
