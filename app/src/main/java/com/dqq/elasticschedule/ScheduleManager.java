package com.dqq.elasticschedule;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by Dooqaqa on 2015/7/5.
 */
public class ScheduleManager {
    private static ScheduleManager instance = null;
    private int current_schedule_index = 0;
    private ScheduleManager() {
    }
    public class Schedule {
        long id;
        String name;
        Calendar dead_line_cur;
        Calendar  dead_line_ori;
        Calendar  established_time;
        ArrayList<Schedule> milestones;
    }
    public class SpecificCalendar extends java.util.Calendar {
        @Override
        public void add(int field, int value) {

        }

        @Override
        protected void computeFields() {

        }

        @Override
        protected void computeTime() {

        }

        @Override
        public int getGreatestMinimum(int field) {
            return 0;
        }

        @Override
        public int getLeastMaximum(int field) {
            return 0;
        }

        @Override
        public int getMaximum(int field) {
            return 0;
        }

        @Override
        public int getMinimum(int field) {
            return 0;
        }

        @Override
        public void roll(int field, boolean increment) {

        }
    }
    private ArrayList<Schedule> data_ = new ArrayList<Schedule>();
    private SQLiteDatabase db_ = null;
    private static final String DATABASE_TABLE = "schlist";
    private void clear(){
        data_.clear();
    }
    public static ScheduleManager GetInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }
    public int GetCurrentScheduleIndex() {
        return current_schedule_index;
    }
    public int GetScheduleCount() {
        return data_.size();
    }
    public void InitDb(SQLiteDatabase db) {
        db_ = db;
        try {
            Log.e("1111111", "InitDb good");
            Cursor c = db_.rawQuery("select name from sqlite_master where type='table' and name like 'schlist';", null);
            if (0 == c.getCount()) {
                String sql = new String();
                sql = "create table " + DATABASE_TABLE + "(_id integer primary key autoincrement, name text not null," +
                        "deadline_ori bigint, deadline_cur bigint, estab_time bigint);";
                Log.e("1111111", "sql: " + sql);
                db_.execSQL(sql);
            }
            c.close();
            LoadData();
        } catch (Exception e) {
            Log.e("1111111c", e.getMessage());
        }
    }

    public java.util.ArrayList GetScheduleList() {
        java.util.ArrayList rt = new java.util.ArrayList();
        for(Schedule s:data_) {
            rt.add(s.name);
        }
        return rt;
    }
    public java.util.ArrayList OpenSchedule(int index) {
        current_schedule_index = index;
        java.util.ArrayList rt = new java.util.ArrayList();
        if (current_schedule_index < data_.size()) {
            if (null != data_.get(current_schedule_index).milestones) {
                for(Schedule s:data_.get(current_schedule_index).milestones) {
                    rt.add(s.name);
                }
            }
            rt.add(data_.get(current_schedule_index).name);
        }
        return rt;
    }

    public String GetScheduleName(int index) {
        return data_.get(index).name;
    }

    private void loadScheduleList() {

    }
    public void AddSchedule(Schedule s) {
        try {
            db_.execSQL("INSERT INTO " + DATABASE_TABLE + " VALUES (NULL, ?, ?, ?, ?)",
                    new Object[]{s.name,
                            null != s.dead_line_ori ? s.dead_line_ori.getTime().getTime() : 0,
                            null != s.dead_line_cur ? s.dead_line_cur.getTime().getTime() : 0,
                            null != s.established_time ? s.established_time.getTime().getTime() : 0});
            clear();
            LoadData();
        } catch (Exception e) {
            Log.e("222222", e.getMessage());
        }
    }
    private void LoadData() {
        LoadScheduleList();
    }
    private void LoadScheduleList() {
        try {
            Cursor c;
            c = db_.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
            Log.e("11111111", "total:" + c.getCount());
            while (c.moveToNext()) {
                Schedule ss = new Schedule();
                ss.id = c.getLong(c.getColumnIndex("_id"));
                ss.name = c.getString(c.getColumnIndex("name"));
                ss.dead_line_ori = Calendar.getInstance();
                ss.dead_line_ori.setTime(new Date(c.getLong(c.getColumnIndex("deadline_ori"))));
                ss.dead_line_cur = Calendar.getInstance();
                ss.dead_line_cur.setTime(new Date(c.getLong(c.getColumnIndex("deadline_cur"))));
                ss.established_time = Calendar.getInstance();
                ss.established_time.setTime(new Date(c.getLong(c.getColumnIndex("estab_time"))));
                Log.e("11111111a", ss.name);
                //Log.e("11111111b", DateFormat.getInstance().format(ss.dead_line.getTime()));
                Log.e("11111111b", ss.established_time.toString());
                LoadMilestones(ss.id, ss);
                data_.add(ss);
            }
        } catch (Exception e) {
            Log.e("33333", e.getMessage());
        }
    }
    private void LoadMilestones(long index, Schedule s) {

    }
    private void DeleteSchedule(long index) {

    }
}
