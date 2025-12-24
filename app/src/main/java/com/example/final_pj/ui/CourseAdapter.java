package com.example.final_pj.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.Course;
import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses = new ArrayList<>();
    private int[] colorRes = {
        R.color.course_1, R.color.course_2, R.color.course_3, 
        R.color.course_4, R.color.course_5, R.color.course_6, R.color.course_7
    };
    private int[] tagColors = {
        0xFF2196F3, 0xFF4CAF50, 0xFFFF9800, 0xFF9C27B0, 0xFF3F51B5, 0xFF009688, 0xFFE91E63
    };

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvName.setText(course.getName());
        holder.tvRoom.setText(course.getRoom());
        holder.tvTime.setText("第" + course.getStartPeriod() + "-" + course.getEndPeriod() + "节");
        
        // 分配颜色
        int colorIdx = Math.abs(course.getName().hashCode()) % colorRes.length;
        holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(colorRes[colorIdx]));
        holder.viewTag.setBackgroundColor(tagColors[colorIdx]);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoom, tvTime;
        CardView cardView;
        View viewTag;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_course_name);
            tvRoom = itemView.findViewById(R.id.tv_course_room);
            tvTime = itemView.findViewById(R.id.tv_course_time);
            cardView = (CardView) itemView.findViewById(R.id.card_course);
            viewTag = itemView.findViewById(R.id.view_color_tag);
        }
    }
}