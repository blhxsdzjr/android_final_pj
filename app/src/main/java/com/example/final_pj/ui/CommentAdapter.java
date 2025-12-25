package com.example.final_pj.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.MarketComment;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    
    public static class CommentNode {
        public MarketComment comment;
        public int depth; // 0 for root, 1 for child

        public CommentNode(MarketComment comment, int depth) {
            this.comment = comment;
            this.depth = depth;
        }
    }

    private List<CommentNode> comments = new ArrayList<>();
    private OnReplyClickListener listener;

    public interface OnReplyClickListener {
        void onReplyClick(MarketComment comment);
    }

    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.listener = listener;
    }

    public void setComments(List<CommentNode> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentNode node = comments.get(position);
        MarketComment comment = node.comment;
        
        holder.tvUser.setText(comment.getUsername());
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getCommentTime());

        // Handle Indentation
        int paddingStart = node.depth * 48; // 48dp indentation per level
        float scale = holder.itemView.getContext().getResources().getDisplayMetrics().density;
        int paddingPx = (int) (paddingStart * scale);
        
        holder.container.setPadding(paddingPx, 0, 0, 0);

        holder.tvReplyAction.setOnClickListener(v -> {
            if (listener != null) listener.onReplyClick(comment);
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvContent, tvTime, tvReplyAction;
        View container;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_comment_user);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvReplyAction = itemView.findViewById(R.id.tv_reply_action);
            container = itemView.findViewById(R.id.container_comment);
        }
    }
}
