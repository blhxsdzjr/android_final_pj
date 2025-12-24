package com.example.final_pj.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.Todo;
import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todos = new ArrayList<>();
    private OnTodoItemClickListener listener;

    public interface OnTodoItemClickListener {
        void onStatusChanged(Todo todo, boolean isDone);
        void onDeleteClick(Todo todo);
    }

    public void setOnTodoItemClickListener(OnTodoItemClickListener listener) {
        this.listener = listener;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.tvContent.setText(todo.getContent());
        holder.tvDeadline.setText("截止: " + todo.getDeadlineTime());
        
        // Temporarily detach listener to avoid triggering it during binding
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(todo.isDone());
        
        // Strike through text if done
        if (todo.isDone()) {
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvContent.setTextColor(0xFF888888); // Gray
        } else {
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvContent.setTextColor(0xFF000000); // Black
        }

        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onStatusChanged(todo, isChecked);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(todo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbDone;
        TextView tvContent, tvDeadline;
        ImageView ivDelete;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            cbDone = itemView.findViewById(R.id.cb_todo_done);
            tvContent = itemView.findViewById(R.id.tv_todo_content);
            tvDeadline = itemView.findViewById(R.id.tv_todo_deadline);
            ivDelete = itemView.findViewById(R.id.iv_delete_todo);
        }
    }
}