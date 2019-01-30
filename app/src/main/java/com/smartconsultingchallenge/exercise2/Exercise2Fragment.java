package com.smartconsultingchallenge.exercise2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartconsultingchallenge.R;

public class Exercise2Fragment extends Fragment {

    public Exercise2Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise2, container, false);

        TextView message = view.findViewById(R.id.message);
        message.setText(R.string.title_exercise_2);

        return view;
    }
}
