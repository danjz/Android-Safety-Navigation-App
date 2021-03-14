package com.example.googlemapsnavbar3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

//        Button button1 = getView().findViewById(R.id.savedDestinationsButton);
//        Button button2 = getView().findViewById(R.id.savedContactsButton);
//        Button button3 = getView().findViewById(R.id.mapSettingsButton);
//        Button button4 = getView().findViewById(R.id.themeSettingsButton);
//        Button button5 = getView().findViewById(R.id.batterySavingSettingsButton);
//        Button button6 = getView().findViewById(R.id.privAndSecButton);
//        Button button7 = getView().findViewById(R.id.helpAndSupportButton);
//        Button button8 = getView().findViewById(R.id.aboutUsButton);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savedDestinationsButton:
               // Toast.makeText(ThirdFragment.class, "Button 1 clicked", LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }
}

