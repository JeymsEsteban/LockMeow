package com.example.lockmeow;

// ConfiFragment.java

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ConfiFragment extends Fragment {

    private SharedViewModel sharedViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confi, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        ImageButton button1 = view.findViewById(R.id.imageButton);
        ImageButton button2 = view.findViewById(R.id.imageButton2);
        ImageButton button3 = view.findViewById(R.id.imageButton3);
        ImageButton button4 = view.findViewById(R.id.imageButton4);
        ImageButton button5 = view.findViewById(R.id.imageButton5);
        ImageButton button6 = view.findViewById(R.id.imageButton6);

        button1.setOnClickListener(v -> sharedViewModel.setButtonClicked(1));
        button2.setOnClickListener(v -> sharedViewModel.setButtonClicked(2));
        button3.setOnClickListener(v -> sharedViewModel.setButtonClicked(3));
        button4.setOnClickListener(v -> sharedViewModel.setButtonClicked(4));
        button5.setOnClickListener(v -> sharedViewModel.setButtonClicked(5));
        button6.setOnClickListener(v -> sharedViewModel.setButtonClicked(6));

        return view;
    }
}
