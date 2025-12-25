package com.example.final_pj.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.MarketPost;
import java.util.ArrayList;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.PostViewHolder> {
    private List<MarketPost> posts = new ArrayList<>();
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(MarketPost post);
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<MarketPost> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        MarketPost post = posts.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvContent.setText(post.getContent());
        holder.tvUser.setText(post.getUsername());
        holder.tvTime.setText(post.getPostTime());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvUser, tvTime;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvContent = itemView.findViewById(R.id.tv_post_content);
            tvUser = itemView.findViewById(R.id.tv_post_user);
            tvTime = itemView.findViewById(R.id.tv_post_time);
        }
    }
}
