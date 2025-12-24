package com.example.final_pj.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (message.getType() == ChatMessage.TYPE_USER) {
            holder.layoutUser.setVisibility(View.VISIBLE);
            holder.layoutAi.setVisibility(View.GONE);
            holder.tvUserContent.setText(message.getContent());
        } else {
            holder.layoutUser.setVisibility(View.GONE);
            holder.layoutAi.setVisibility(View.VISIBLE);
            holder.tvAiContent.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutUser, layoutAi;
        TextView tvUserContent, tvAiContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUser = itemView.findViewById(R.id.layout_user);
            layoutAi = itemView.findViewById(R.id.layout_ai);
            tvUserContent = itemView.findViewById(R.id.tv_user_content);
            tvAiContent = itemView.findViewById(R.id.tv_ai_content);
        }
    }
}