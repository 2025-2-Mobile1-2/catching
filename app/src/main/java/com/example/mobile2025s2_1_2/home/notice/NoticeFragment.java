package com.example.mobile2025s2_1_2.home.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;

import java.util.List;

public class NoticeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_notice, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.home_notice_card);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<NoticeCardData.HomeNoticeData> list =
                NoticeCardData.loadHomeNotices(requireContext());

        NoticeCardAdapter adapter = new NoticeCardAdapter(requireContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }
}