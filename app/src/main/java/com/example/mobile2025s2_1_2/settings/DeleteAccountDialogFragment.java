package com.example.mobile2025s2_1_2.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class DeleteAccountDialogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_delete_account_dialog, container, false);
        TextView delete = view.findViewById(R.id.account_delete_ok);
        delete.setOnClickListener(v ->{
            SettingsFragment.touchBlocker.setVisibility(View.GONE);
        });

        //취소버틍
        TextView deleteCancel = view.findViewById(R.id.account_delete_cancel);
        deleteCancel.setOnClickListener(v -> {
            SettingsFragment.touchBlocker.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
