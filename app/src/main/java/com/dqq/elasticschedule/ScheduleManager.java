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
    private ArrayList<ScheduleObserver> observer_list = new ArrayList<ScheduleObserver>();
    private ScheduleManager() {
    }
    public class Schedule {
        public int MilestoneCount() {
            int rt = 0;
            if (null != milestones) {
                rt = milestones.size();
            }
            return rt;
        }
        protected long id;
        protected String name;
        protected Calendar dead_line_cur;
        protected Calendar  dead_line_ori;
        protected Calendar  established_time;
        protected ArrayList<Schedule> milestones;
    }
    private ArrayList<Schedule> data_ = new ArrayList<Schedule>();
    private SQLiteDatabase db_ = null;
    private static final String DATABASE_TABLE = "schlist";
    private void clear(){
        data_.clear();
        current_schedule_index = 0;
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
    public String GetScheduleName(int index) {
        if (data_.size() <= index) return "";
        return data_.get(index).name;
    }
    public Schedule GetSchedule(int index) {
        Schedule rt = new Schedule();
        if (index < data_.size()) rt = data_.get(index);
        return rt;
    }
    public Schedule GetCurrentSchedule() {
        return GetSchedule(current_schedule_index);
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
        NotifyScheduleOpened();
        return rt;
    }
    public void InitDb(SQLiteDatabase db) {
        db_ = db;
        clear();
        try {
            Cursor c = db_.rawQuery("select name from sqlite_master where type='table' and name like 'schlist';", null);
            if (0 == c.getCount()) {
                String sql = new String();
                sql = "create table " + DATABASE_TABLE + "(_id integer , name text not null," +
                        "deadline_ori bigint, deadline_cur bigint, estab_time bigint);";
                Log.e("InitDb", "sql: " + sql);
                db_.execSQL(sql);
            }
            c.close();
            LoadData();
        } catch (Exception e) {
            Log.e("InitDb_e", e.getMessage());
        }
    }

    public void AddSchedule(Schedule s, int index) {
        try {
            db_.execSQL("INSERT INTO " + DATABASE_TABLE + " VALUES (?, ?, ?, ?, ?)",
                    new Object[]{index, s.name,
                            null != s.dead_line_ori ? s.dead_line_ori.getTime().getTime() : 0,
                            null != s.dead_line_cur ? s.dead_line_cur.getTime().getTime() : 0,
                            null != s.established_time ? s.established_time.getTime().getTime() : 0});
            clear();
            LoadData();
        } catch (Exception e) {
            Log.e("AddSchedule_e", e.getMessage());
        }
    }
    public void AddMileStone(Schedule m, int index) {
        if (current_schedule_index >= data_.size()) return;
        Schedule s = data_.get(current_schedule_index);
        if (null == s.milestones) {
            s.milestones = new ArrayList<Schedule>();
            String sql = new String();
            sql = "CREATE TABLE m_" + s.id + " (_id integer , name text not null," +
                    "deadline_ori bigint, deadline_cur bigint, estab_time bigint);";
            Log.e("AddMileStone", "sql: " + sql);
            db_.execSQL(sql);
        }
        db_.execSQL("INSERT INTO m_" + s.id + " VALUES (?, ?, ?, ?, ?)",
                new Object[]{index, m.name,
                        null != m.dead_line_ori ? m.dead_line_ori.getTime().getTime() : 0,
                        null != m.dead_line_cur ? m.dead_line_cur.getTime().getTime() : 0,
                        null != m.established_time ? m.established_time.getTime().getTime() : 0});
        data_.get(current_schedule_index).milestones.add(m);
    }

    private void LoadData() {
        LoadScheduleList();
    }
    private void LoadScheduleList() {
        try {
            Cursor c;
            c = db_.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
            Log.e("LoadScheduleList", "total:" + c.getCount());
            while (c.moveToNext()) {
                Schedule s = new Schedule();
                s.id = c.getLong(c.getColumnIndex("_id"));
                s.name = c.getString(c.getColumnIndex("name"));
                s.dead_line_ori = Calendar.getInstance();
                s.dead_line_ori.setTime(new Date(c.getLong(c.getColumnIndex("deadline_ori"))));
                s.dead_line_cur = Calendar.getInstance();
                s.dead_line_cur.setTime(new Date(c.getLong(c.getColumnIndex("deadline_cur"))));
                s.established_time = Calendar.getInstance();
                s.established_time.setTime(new Date(c.getLong(c.getColumnIndex("estab_time"))));
                LoadMilestones(s);
                data_.add(s);
            }
        } catch (Exception e) {
            Log.e("LoadScheduleList_e", e.getMessage());
        }
    }
    private void LoadMilestones( Schedule s) {
        try {
            Cursor c;
            c = db_.rawQuery("SELECT * FROM m_" + s.id + " order by _id", null);
            Log.e("LoadMilestones", "total:" + c.getCount());
            if (null == s.milestones) s.milestones = new ArrayList<Schedule>();
            while (c.moveToNext()) {
                Schedule m = new Schedule();
                m.id = c.getLong(c.getColumnIndex("_id"));
                m.name = c.getString(c.getColumnIndex("name"));
                m.dead_line_ori = Calendar.getInstance();
                m.dead_line_ori.setTime(new Date(c.getLong(c.getColumnIndex("deadline_ori"))));
                m.dead_line_cur = Calendar.getInstance();
                m.dead_line_cur.setTime(new Date(c.getLong(c.getColumnIndex("deadline_cur"))));
                m.established_time = Calendar.getInstance();
                m.established_time.setTime(new Date(c.getLong(c.getColumnIndex("estab_time"))));
                s.milestones.add(m);
            }
        } catch (Exception e) {
            Log.e("LoadMilestones_e", e.getMessage());
        }
    }
    public void DeleteSchedule(long index) {
        if (data_.size() <= index) return;
        try {
            db_.execSQL("DELETE FROM " + DATABASE_TABLE + " where _id=" + data_.get((int) index).id, new Object[]{});
            if (null != data_.get((int)index).milestones) {
                db_.execSQL("DROP TABLE m_" + data_.get((int)index).id, new Object[]{});
            }
            clear();
            LoadData();
            NotifyScheduleDeleted(index);
        } catch (Exception e) {
            Log.e("DeleteSchedule_e", e.getMessage());
        }
    }
    public void UpdateSchedule(Schedule s) {

    }

    public void MoveMilesone(int sourcepos, int targetpos) {
        Schedule s = GetCurrentSchedule();
        if (null != s.milestones && sourcepos <s.milestones.size() && targetpos < s.milestones.size()) {
            Schedule m = s.milestones.get(sourcepos);
            s.milestones.remove(sourcepos);
            s.milestones.add(targetpos, m);
        }
        try {
            int tempid = s.milestones.size();
            int dbsource = sourcepos;
            int dbtarget = targetpos;
            db_.beginTransaction();
            if (sourcepos > targetpos) {
                String sql = "update m_" + s.id + " set _id=" + tempid + " where _id=" + dbsource + ";";
                Log.e("11111111MoveMilesone", sql);
                db_.execSQL(sql, new Object[]{});
                sql = "update m_" + s.id + " set _id=_id+1 where _id<" + dbsource + " and _id>=" + dbtarget + ";";
                Log.e("11111111MoveMilesone", sql);
                db_.execSQL(sql, new Object[]{});
                sql = "update m_" + s.id + " set _id=" + dbtarget + " where _id=" + tempid + ";";
                Log.e("11111111MoveMilesone", sql);
                db_.execSQL(sql, new Object[]{});
            } else {
                String sql = "update m_" + s.id + " set _id=" + tempid + " where _id=" + dbsource + ";";
                Log.e("11111111MoveMilesone", sql);
                db_.execSQL(sql, new Object[]{});
                sql = "update m_" + s.id + " set _id=_id-1 where _id>" + dbsource + " and _id<=" + dbtarget + ";";
                Log.e("11111111MoveMilesone", sql);
                db_.execSQL(sql, new Object[]{});
                sql = "update m_" + s.id + " set _id=" + dbtarget + " where _id=" + tempid + ";";
                Log.e("11111111MoveMilesone", sql);
                db_.execSQL(sql, new Object[]{});
            }
            db_.setTransactionSuccessful();
            db_.endTransaction();
        } catch (Exception e) {
            Log.e("MoveMilesone", e.getMessage());
            db_.endTransaction();
        }
    }
    public void RegObserver(ScheduleObserver ob,boolean isreg) {
        if (isreg) {
            observer_list.add(ob);
        } else {
            observer_list.remove(ob);
        }
    }
    private void NotifyScheduleOpened() {
        for (ScheduleObserver ob : observer_list) {
            ob.NotifyScheduleOpened(current_schedule_index);
        }
    }
    private void NotifyScheduleDeleted(long index) {
        for (ScheduleObserver ob : observer_list) {
            ob.NotifyScheduleDeleted(index);
        }
    }
}
