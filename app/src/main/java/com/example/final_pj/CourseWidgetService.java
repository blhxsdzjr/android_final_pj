package com.example.final_pj;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Course;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CourseRemoteViewsFactory(this.getApplicationContext());
    }
}

class CourseRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Course> mCourses = new ArrayList<>();

    public CourseRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        // 在后台线程查询今天的课程
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int chineseDay = (dayOfWeek == 1) ? 7 : dayOfWeek - 1;

        // 这里我们简化一下，直接获取所有课程然后手动过滤（因为小组件是在后台更新）
        // 假设当前是第 1 周 (实际应从 Prefs 读取)
        int currentWeek = mContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("current_week", 1);
        
        List<Course> all = AppDatabase.getInstance(mContext).courseDao().getAllCourses();
        mCourses.clear();
        for (Course c : all) {
            if (c.getDayOfWeek() == chineseDay && currentWeek >= c.getStartWeek() && currentWeek <= c.getEndWeek()) {
                mCourses.add(c);
            }
        }
    }

    @Override
    public void onDestroy() { mCourses.clear(); }

    @Override
    public int getCount() { return mCourses.size(); }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= mCourses.size()) return null;

        Course course = mCourses.get(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_course);
        rv.setTextViewText(R.id.tv_widget_course_name, course.getName());
        rv.setTextViewText(R.id.tv_widget_course_time, "第" + course.getStartPeriod() + "-" + course.getEndPeriod() + "节 | " + course.getRoom());

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() { return null; }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public boolean hasStableIds() { return true; }
}