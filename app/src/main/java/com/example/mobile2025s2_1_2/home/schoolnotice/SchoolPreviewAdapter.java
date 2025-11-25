package com.example.mobile2025s2_1_2.home.schoolnotice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;

import java.util.List;

public class SchoolPreviewAdapter extends RecyclerView.Adapter<SchoolPreviewAdapter.ViewHolder> {

    private List<SchoolCardData> list;
    private Context context;

    // 클릭 이벤트 전달용
    public interface OnItemClickListener {
        void onClick(SchoolCardData item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SchoolPreviewAdapter(Context context, List<SchoolCardData> list) {
        this.context = context;
        this.list = list;
    }

    public void updateData(List<SchoolCardData> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchoolPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_school_notice_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolPreviewAdapter.ViewHolder holder, int position) {
        SchoolCardData item = list.get(position);

        holder.title.setText(item.getTitle());
        holder.title.setSelected(false); // 자동 스크롤(마키) 방지

        holder.layout.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.home_school_layout);
            title = itemView.findViewById(R.id.home_school_title);
            icon = itemView.findViewById(R.id.home_school_icon);
        }
    }
}