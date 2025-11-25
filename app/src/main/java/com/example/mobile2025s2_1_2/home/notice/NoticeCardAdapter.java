package com.example.mobile2025s2_1_2.home.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;

import java.util.List;

public class NoticeCardAdapter extends RecyclerView.Adapter<NoticeCardAdapter.NoticeViewHolder> {

    private List<NoticeCardData.HomeNoticeData> noticeList;
    private Context context;

    public NoticeCardAdapter(Context context, List<NoticeCardData.HomeNoticeData> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_notice_card, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeCardData.HomeNoticeData item = noticeList.get(noticeList.size() - 1 - position);

        holder.title.setText(item.getTitle());
        holder.text.setText(item.getNoticeText());

        // 클릭 시 세부 텍스트 펼치기/닫기
        holder.layout.setOnClickListener(v -> {
            if (holder.text.getVisibility() == View.GONE) {
                holder.text.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(R.drawable.ic_arrow_down);
            } else {
                holder.text.setVisibility(View.GONE);
                holder.icon.setImageResource(R.drawable.ic_arrow_right_mint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {

        TextView title, text;
        ImageView icon;
        View layout;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.home_notice_layout);
            title = itemView.findViewById(R.id.home_notice_title);
            text = itemView.findViewById(R.id.home_notice_text);
            icon = itemView.findViewById(R.id.home_notice_icon);
        }
    }
}