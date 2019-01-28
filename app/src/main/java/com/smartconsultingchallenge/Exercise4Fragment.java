package com.smartconsultingchallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Exercise4Fragment extends Fragment {

    public Exercise4Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        TextView message = view.findViewById(R.id.message);
        message.setText(String.format("%s - %s", getActivity().getString(R.string.title_exercise_4), BuildConfig.URL));

        return view;
    }
}
