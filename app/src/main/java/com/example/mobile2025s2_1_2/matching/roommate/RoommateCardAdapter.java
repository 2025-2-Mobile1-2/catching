package com.example.mobile2025s2_1_2.matching.roommate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile2025s2_1_2.R;

import java.util.List;


public class RoommateCardAdapter extends RecyclerView.Adapter<RoommateCardAdapter.ViewHolder> {

    private Context context;
    private List<RoommateCardData.RoommateData> roommateList; // JSON 데이터 리스트

    // 🔹 생성자
    public RoommateCardAdapter(Context context, List<RoommateCardData.RoommateData> roommateList) {
        this.context = context;
        this.roommateList = roommateList;
    }

    // 🔹 카드 아이템 하나를 표현하는 내부 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView majorText;
        TextView mbtiText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.roommate_name);
            majorText = itemView.findViewById(R.id.roommate_major);
            mbtiText = itemView.findViewById(R.id.roommate_mbti);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.matching_roommate_profile_card, parent, false); // 카드 XML 연결
        return new ViewHolder(view);
    }

    //JSON 데이터 연결
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoommateCardData.RoommateData data = roommateList.get(position);
        holder.nameText.setText(data.getName());
        holder.majorText.setText(data.getMajor());
        holder.mbtiText.setText(data.getMbti());
    }

    // 🔹 전체 카드 개수
    @Override
    public int getItemCount() {
        return roommateList.size();
    }
}