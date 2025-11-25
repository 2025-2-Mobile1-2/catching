package com.example.mobile2025s2_1_2.matching.roommate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;

import java.util.List;

public class RoommateCardAdapter extends RecyclerView.Adapter<RoommateCardAdapter.ViewHolder> {

    private Context context;
    private List<RoommateCardData.RoommateData> roommateList;

    public RoommateCardAdapter(Context context, List<RoommateCardData.RoommateData> roommateList) {
        this.context = context;
        this.roommateList = roommateList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView sexText;
        TextView dormitoryText;
        TextView ageText;
        TextView mbtiText;
        TextView drinkText;
        TextView smokeText;

        // ⭐ 수정된 부분 — SeekBar + 숫자 TextView
        SeekBar cleanSeekBar;
        SeekBar sleepSeekBar;
        SeekBar sensitiveSeekBar;

        TextView cleanValue;
        TextView sleepValue;
        TextView sensitiveValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.roommate_name);
            sexText = itemView.findViewById(R.id.roommate_sex);
            dormitoryText = itemView.findViewById(R.id.roommate_dormitory);
            ageText = itemView.findViewById(R.id.roommate_age);
            mbtiText = itemView.findViewById(R.id.roommate_mbti);
            drinkText = itemView.findViewById(R.id.roommate_drink);
            smokeText = itemView.findViewById(R.id.roommate_smoke);

            // ⭐ XML에 있는 새로운 id들로 변경
            cleanSeekBar = itemView.findViewById(R.id.roommate_clean_seekbar);
            sleepSeekBar = itemView.findViewById(R.id.roommate_sleep_seekbar);
            sensitiveSeekBar = itemView.findViewById(R.id.roommate_sensitive_seekbar);

            cleanValue = itemView.findViewById(R.id.roommate_clean_value);
            sleepValue = itemView.findViewById(R.id.roommate_sleep_value);
            sensitiveValue = itemView.findViewById(R.id.roommate_sensitive_value);
        }
    }

    @NonNull
    @Override
    public RoommateCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.matching_roommate_profile_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoommateCardAdapter.ViewHolder holder, int position) {

        RoommateCardData.RoommateData data = roommateList.get(position);

        // 기본 프로필 텍스트들
        holder.nameText.setText(data.getName());
        holder.sexText.setText(data.getGender());
        holder.dormitoryText.setText(data.getDorm());
        holder.ageText.setText(data.getAge());
        holder.mbtiText.setText(data.getMbti());
        holder.drinkText.setText("음주 " + data.getAlcohol());
        holder.smokeText.setText("흡연 " + data.getSmoking());

        // 문자열 → int 변환
        int clean = parseInt(data.getClean());
        int sleep = parseInt(data.getSleep());
        int sensitive = parseInt(data.getSensitive());

        // ⭐ SeekBar + 숫자 적용
        holder.cleanSeekBar.setProgress(clean);
        holder.sleepSeekBar.setProgress(sleep);
        holder.sensitiveSeekBar.setProgress(sensitive);

        holder.cleanValue.setText(String.valueOf(clean));
        holder.sleepValue.setText(String.valueOf(sleep));
        holder.sensitiveValue.setText(String.valueOf(sensitive));
    }

    @Override
    public int getItemCount() {
        return roommateList.size();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
