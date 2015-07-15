package com.dqq.elasticschedule;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dooqaqa on 2015/7/5.
 */
public class ScheduleManager {
    private static ScheduleManager instance = null;
    private int current_schedule_index = 0;
    private ScheduleManager() {
    }
    public class Schedule {
        int id;
        String name;
        Date dead_line;
        ArrayList<Schedule> milestones;
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
                sql = "create table " + DATABASE_TABLE + "(_id integer primary key autoincrement, name text not null, deadline_ori date, deadline_cur date);";
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
        if (current_schedule_index < data_.size() && null != data_.get(current_schedule_index).milestones) {
            for(Schedule s:data_.get(current_schedule_index).milestones) {
                rt.add(s.name);
            }
        }
        return rt;
    }

    public String GetScheduleName(int index) {
        return "目标" + index;
    }

    private void loadScheduleList() {

    }
    public void AddSchedule(Schedule s) {
        try {
            db_.execSQL("INSERT INTO " + DATABASE_TABLE + " VALUES (NULL, ?, ?, ?)", new Object[]{s.name, s.dead_line, s.dead_line});
            clear();
            LoadData();
        } catch (Exception e) {
            Log.e("222222", e.getMessage());
        }
    }
    private void LoadData() {
        try {
            Cursor c;
            c = db_.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
            Log.e("11111111", "total:" + c.getCount());
            while (c.moveToNext()) {
                Schedule ss = new Schedule();
                ss.id = c.getInt(c.getColumnIndex("_id"));
                ss.name = c.getString(c.getColumnIndex("name"));
                ss.dead_line = new Date(c.getString(c.getColumnIndex("deadline_cur")));
                Log.e("11111111a", ss.name);
                Log.e("11111111b", ss.dead_line.toString());
                data_.add(ss);
            }
        } catch (Exception e) {
            Log.e("33333", e.getMessage());
        }
    }
}
