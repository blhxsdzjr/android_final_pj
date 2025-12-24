package com.example.final_pj.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.Course;
import java.util.ArrayList;
import java.util.List;

public class ManageCourseAdapter extends RecyclerView.Adapter<ManageCourseAdapter.ViewHolder> {
    private List<Course> courses = new ArrayList<>();
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Course course);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.listener = listener;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvName.setText(course.getName());
        
        String dayStr = getDayString(course.getDayOfWeek());
        holder.tvInfo.setText(dayStr + " | 第" + course.getStartPeriod() + "-" + course.getEndPeriod() + "节 | " + course.getRoom());
        holder.tvWeeks.setText("第" + course.getStartWeek() + "-" + course.getEndWeek() + "周");

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(course);
            }
        });
    }

    private String getDayString(int day) {
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        if (day >= 1 && day <= 7) return days[day-1];
        return "";
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo, tvWeeks;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_course_name);
            tvInfo = itemView.findViewById(R.id.tv_course_info);
            tvWeeks = itemView.findViewById(R.id.tv_course_weeks);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}