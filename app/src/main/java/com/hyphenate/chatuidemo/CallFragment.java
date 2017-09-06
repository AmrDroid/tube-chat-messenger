package com.hyphenate.chatuidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chatuidemo.settings.SettingsFragment;
import com.hyphenate.chatuidemo.settings.SettingsPreference;

/**
 * Created by amrr on 9/1/2017.
 */

public class CallFragment extends Fragment {

    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance() {
        CallFragment fragment = new CallFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.em_fragment_call, container, false);
        init();
        return view;
    }

    private void init() {

        // Display the fragment as the Fmain content.
        getActivity().getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsPreference())
                .commit();
    }

}
