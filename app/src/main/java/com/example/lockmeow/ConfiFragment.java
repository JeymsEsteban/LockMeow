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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    private DatabaseReference database;
    String uid;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confi, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        ImageButton button1 = view.findViewById(R.id.imageButton);
        ImageButton button2 = view.findViewById(R.id.imageButton2);
        ImageButton button3 = view.findViewById(R.id.imageButton3);
        ImageButton button4 = view.findViewById(R.id.imageButton4);
        ImageButton button5 = view.findViewById(R.id.imageButton5);
        ImageButton button6 = view.findViewById(R.id.imageButton6);
        if (user != null) {
            // Almacenar datos del usuario en la base de datos
            uid = user.getUid();
            button1.setOnClickListener(v -> database.child("users").child(uid).child("Gato").setValue("1"));
            button2.setOnClickListener(v -> database.child("users").child(uid).child("Gato").setValue("2"));
            button3.setOnClickListener(v -> database.child("users").child(uid).child("Gato").setValue("3"));
            button4.setOnClickListener(v -> database.child("users").child(uid).child("Gato").setValue("4"));
            button5.setOnClickListener(v -> database.child("users").child(uid).child("Gato").setValue("5"));
            button6.setOnClickListener(v -> database.child("users").child(uid).child("Gato").setValue("6"));
        }

        return view;
    }
}
