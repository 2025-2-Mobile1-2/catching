package com.example.mobile2025s2_1_2.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;
import java.util.List;

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.ViewHolder> {

    //이미지 리소스 ID 목록을 저장하는 리스트 (예: R.drawable.test 등)
    private List<Integer> imageList;

    //생성자 — 이미지 리스트를 받아 저장
    public HomeCardAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    //ViewHolder: XML의 각 카드 아이템을 담는 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage); // ✅ XML에서 id 연결
        }
    }

    // XML(home_extracurricula_card.xml)을 View로 변환
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_extracurricula_card, parent, false);
        return new ViewHolder(view);
    }

    // 각 카드에 데이터(이미지)를 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageRes = imageList.get(position); // 리스트에서 해당 이미지 꺼내기
        holder.cardImage.setImageResource(imageRes); // ✅ 이미지뷰에 설정
    }

    // 전체 카드 개수 반환
    @Override
    public int getItemCount() {
        return imageList.size();
    }
}