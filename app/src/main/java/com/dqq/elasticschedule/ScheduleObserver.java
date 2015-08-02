package com.dqq.elasticschedule;

/**
 * Created by dooqaqa on 2015/7/21.
 */
public interface ScheduleObserver {
    public void  NotifyScheduleOpened(long index);
    public void  NotifyScheduleListChanged();
    public void NotifyScheduleDeleted(long index);
}
