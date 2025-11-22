package com.example.mobile2025s2_1_2.matching.roommate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile2025s2_1_2.R;
import com.google.android.material.card.MaterialCardView;

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
        TextView sexText;
        TextView dormitoryText;
        TextView ageText;
        TextView mbtiText;
        TextView drinkText;
        TextView smokeText;
        MaterialCardView cleanPercent;
        MaterialCardView sleepPercent;
        MaterialCardView subtletyPercent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.roommate_name);
            sexText = itemView.findViewById(R.id.roommate_sex);
            dormitoryText = itemView.findViewById(R.id.roommate_dormitory);
            ageText = itemView.findViewById(R.id.roommate_age);
            mbtiText = itemView.findViewById(R.id.roommate_mbti);
            drinkText = itemView.findViewById(R.id.roommate_drink);
            smokeText = itemView.findViewById(R.id.roommate_smoke);
            cleanPercent = itemView.findViewById(R.id.roommate_clean);
            sleepPercent = itemView.findViewById(R.id.roommate_sleep);
            subtletyPercent = itemView.findViewById(R.id.roommate_subtlety);
        }
    }

    private void setPercent(MaterialCardView bar, int percent) {
        percent = Math.max(0, Math.min(percent, 100));
        int finalPercent = percent;
        bar.post(() -> {
            int parentWidth = dpToPx(220);  // 최대 길이를 220dp로 고정
            ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.width = (int)(parentWidth * (finalPercent / 100f));
            bar.setLayoutParams(params);
        });
    }
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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
        holder.sexText.setText(data.getSex());
        holder.dormitoryText.setText(data.getDormitory());
        holder.ageText.setText(data.getAge());
        holder.mbtiText.setText(data.getMbti());
        holder.drinkText.setText(data.getDrink());
        holder.smokeText.setText(data.getSmoke());
        setPercent(holder.cleanPercent, data.getClean());
        setPercent(holder.sleepPercent, data.getSleep());
        setPercent(holder.subtletyPercent, data.getSubtlety());
    }

    // 🔹 전체 카드 개수
    @Override
    public int getItemCount() {
        return roommateList.size();
    }
}