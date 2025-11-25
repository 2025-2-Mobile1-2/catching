package com.example.mobile2025s2_1_2.home.schoolnotice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.home.notice.NoticeCardAdapter;
import com.example.mobile2025s2_1_2.home.notice.NoticeCardData;

import java.util.List;

public class SchoolFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_school_notice, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.home_school_card);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //학사공지 크롤링 후 RecyclerView 연결
        SchoolCardAdapter adapter = new SchoolCardAdapter(requireContext(), null);
        recyclerView.setAdapter(adapter);

        new Thread(() -> {
            List<SchoolCardData> data = SchoolCrawler.fetchNotices();

            requireActivity().runOnUiThread(() -> {
                adapter.updateData(data);
            });
        }).start();

        //뒤로가기
        ImageView roommateBack = view.findViewById(R.id.home_school_back);
        roommateBack.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}