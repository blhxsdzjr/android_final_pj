package com.example.final_pj.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.final_pj.AddCourseActivity;
import com.example.final_pj.R;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Course;
import com.example.final_pj.data.CourseRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseTableFragment extends Fragment {

    private RelativeLayout rlCourseContainer;
    private LinearLayout llSidebar, llGridLines;
    private TextView tvWeekTitle, tvManage;
    private FloatingActionButton fabAdd;
    private CourseRepository courseRepository;
    
    // Config
    private static final int PERIOD_HEIGHT_DP = 60;
    private static final int TOTAL_PERIODS = 12;
    private int periodHeightPx;
    private int columnWidthPx;
    private int currentWeek = 1;

    // Colors
    private int[] courseColors = {
        0xFFE3F2FD, 0xFFF1F8E9, 0xFFFFF3E0, 
        0xFFF3E5F5, 0xFFE8EAF6, 0xFFE0F2F1, 0xFFFBE9E7
    };
    private int[] textColors = {
        0xFF1565C0, 0xFF2E7D32, 0xFFEF6C00, 
        0xFF6A1B9A, 0xFF283593, 0xFF00695C, 0xFFD84315
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_table, container, false);

        courseRepository = new CourseRepository(requireActivity().getApplication());

        rlCourseContainer = view.findViewById(R.id.rl_course_container);
        llSidebar = view.findViewById(R.id.ll_sidebar);
        llGridLines = view.findViewById(R.id.ll_grid_lines);
        tvWeekTitle = view.findViewById(R.id.tv_week_title);
        tvManage = view.findViewById(R.id.tv_manage_courses);
        fabAdd = view.findViewById(R.id.fab_add);

        // Convert DP to PX
        periodHeightPx = dpToPx(PERIOD_HEIGHT_DP);
        
        // Setup Grid
        setupGrid();

        fabAdd.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddCourseActivity.class)));
        tvManage.setOnClickListener(v -> startActivity(new Intent(getActivity(), com.example.final_pj.ManageCoursesActivity.class)));

        // Load Prefs
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        currentWeek = prefs.getInt("current_week", 1);
        tvWeekTitle.setText("第 " + currentWeek + " 周");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCourses();
    }

    private void setupGrid() {
        // Calculate column width dynamically
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int sidebarWidth = dpToPx(30); // Width of sidebar
        columnWidthPx = (screenWidth - sidebarWidth) / 7;

        // Draw Sidebar & Grid Lines
        llSidebar.removeAllViews();
        llGridLines.removeAllViews();

        for (int i = 1; i <= TOTAL_PERIODS; i++) {
            // Sidebar Number
            TextView tvNum = new TextView(getContext());
            tvNum.setText(String.valueOf(i));
            tvNum.setGravity(Gravity.CENTER);
            tvNum.setHeight(periodHeightPx);
            tvNum.setTextSize(12);
            tvNum.setTextColor(Color.GRAY);
            llSidebar.addView(tvNum);
        }
        
        llGridLines.removeAllViews();
        for (int i = 0; i < TOTAL_PERIODS; i++) {
             View row = new View(getContext());
             LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, periodHeightPx);
             row.setLayoutParams(params);
             row.setBackgroundResource(R.drawable.bg_grid_row);
             llGridLines.addView(row);
        }
    }

    private void loadCourses() {
        courseRepository.getAllCourses(courses -> {
            // This runs on main thread
            for (int i = rlCourseContainer.getChildCount() - 1; i >= 0; i--) {
                View child = rlCourseContainer.getChildAt(i);
                if (child instanceof CardView) {
                    rlCourseContainer.removeViewAt(i);
                }
            }
            
            for (Course course : courses) {
                if (currentWeek >= course.getStartWeek() && currentWeek <= course.getEndWeek()) {
                    addCourseBlock(course);
                }
            }
        });
    }

    private void addCourseBlock(Course course) {
        int day = course.getDayOfWeek() - 1; // 0-6 (Mon-Sun)
        int startPeriod = course.getStartPeriod() - 1; // 0-based
        int endPeriod = course.getEndPeriod();
        int span = endPeriod - startPeriod; // Number of periods

        if (day < 0 || day > 6) return;

        CardView card = new CardView(requireContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                columnWidthPx - dpToPx(4), // Leave some margin
                periodHeightPx * span - dpToPx(4)
        );
        
        params.leftMargin = day * columnWidthPx + dpToPx(2);
        params.topMargin = (course.getStartPeriod() - 1) * periodHeightPx + dpToPx(2);
        
        card.setLayoutParams(params);
        card.setRadius(dpToPx(8));
        card.setCardElevation(dpToPx(2));
        
        int colorIdx = Math.abs(course.getName().hashCode()) % courseColors.length;
        card.setCardBackgroundColor(courseColors[colorIdx]);

        TextView tv = new TextView(getContext());
        tv.setText(course.getName() + "\n@" + course.getRoom());
        tv.setTextSize(10);
        tv.setTextColor(textColors[colorIdx]);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(4, 4, 4, 4);
        
        card.addView(tv);
        rlCourseContainer.addView(card);
        
        card.setOnClickListener(v -> {
            // Show detail dialog
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
