package org.unimelb.itime.test.bean;

import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public class Event implements ITimeEventInterface{


    @Override
    public void setTitle(String title) {

    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setStartTime(long startTime) {

    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public void setEndTime(long endTime) {

    }

    @Override
    public long getEndTime() {
        return 0;
    }

    @Override
    public void setEventType(int typeId) {

    }

    @Override
    public int getEventType() {
        return 0;
    }

    @Override
    public void setStatus(int statusId) {

    }

    @Override
    public int getStatus() {
        return 0;
    }
}
