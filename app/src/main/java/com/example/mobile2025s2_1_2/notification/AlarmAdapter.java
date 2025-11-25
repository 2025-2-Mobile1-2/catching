package com.example.mobile2025s2_1_2.notification;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.VH> {

    private final List<AlarmItem> items;
    private final boolean isReceivedList;          // 받은/보낸 구분
    private final OnAlarmClickListener listener;   // 클릭 리스너

    // 클릭 리스너 인터페이스
    public interface OnAlarmClickListener {
        void onClick(AlarmItem item, boolean isReceivedList);
    }

    public AlarmAdapter(List<AlarmItem> items,
                        boolean isReceivedList,
                        OnAlarmClickListener listener) {
        this.items = items;
        this.isReceivedList = isReceivedList;
        this.listener = listener;
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title, badge;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_alarm_title);
            badge = itemView.findViewById(R.id.badgeNew);
        }

        void bind(AlarmItem item) {
            // 텍스트 표시 (카테고리 강조)
            title.setText(highlightCategory(item.text));

            // N 뱃지 보이기/숨기기 (데이터만 보고 결정)
            badge.setVisibility(item.isNew ? View.VISIBLE : View.GONE);

            // 클릭 → Fragment 쪽으로 이벤트 전달
            itemView.setOnClickListener(v -> {
                // 여기서는 상태 변경 X, 그냥 콜백만
                if (listener != null) {
                    listener.onClick(item, isReceivedList);
                }
                // 뱃지 UI 갱신은 Fragment에서 item.isNew 바꾼 뒤
                // 필요하면 adapter.notifyItemChanged(position)으로 반영할 수 있음
            });
        }

        private CharSequence highlightCategory(String text) {
            if (text == null) return "";
            SpannableString sp = new SpannableString(text);
            String[] keys = {
                    "기숙사 룸메이트 매칭",
                    "진로·전공 멘토 매칭",
                    "교내·교외 활동 팀원 매칭"
            };
            int mint = Color.parseColor("#2DD7A4");

            for (String k : keys) {
                int start = text.indexOf(k);
                if (start != -1) {
                    int end = start + k.length();
                    sp.setSpan(new ForegroundColorSpan(mint), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return sp;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_n, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
