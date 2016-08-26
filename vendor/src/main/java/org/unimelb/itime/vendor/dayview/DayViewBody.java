package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;


import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayViewBody extends RelativeLayout {
    private static final String TAG = "MyAPP";

    public final LayoutInflater mInflater;
    public RelativeLayout timeRLayout;
    public RelativeLayout dividerRLayout;
    public ScrollContainerView scrollContainerView;

    public DayViewBodyController dayViewController;

    public MyCalendar myCalendar;

    public DayViewBody(Context context) {
        this(context, null);
    }

    public DayViewBody(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.calendarViewStyle);
    }

    public DayViewBody(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInflater = LayoutInflater.from(context);
        inflate(context, R.layout.day_view_body, this);
//        setOrientation(VERTICAL);
        dayViewController = new DayViewBodyController(attrs, context);
    }

    public MyCalendar getCalendar() {
        return myCalendar;
    }

    public void setCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }

    public void addEvent(Event new_event){
        dayViewController.addEvent(new_event);
    }

    public void removeEvent(Event delete_event){
        dayViewController.removeEvent(delete_event);
    }

    public void updateEvent(Event old_event, Event new_event){
        dayViewController.updateEvent(old_event, new_event);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        scrollContainerView = (ScrollContainerView) findViewById(R.id.customer_day_view);
        timeRLayout = (RelativeLayout) findViewById(R.id.timeReLayout);
        dividerRLayout = (RelativeLayout) findViewById(R.id.eventRelativeLayout);

        dayViewController.onFinishInflate(scrollContainerView,timeRLayout, dividerRLayout, this);
    }
      

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        dayViewController.resetViews();

        dayViewController.initBackgroundView();

        dividerRLayout.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Event> events = simulateEvent();
                for (Event event: events
                     ) {
                    dayViewController.addEvent(event);
                }

                dayViewController.reDrawEvents();
            }
        });

        if (myCalendar.isToday()){
            dayViewController.addNowTimeLine();
        }

        dividerRLayout.invalidate();
    }

    private ArrayList<Event> simulateEvent(){
        String[] titles = {"This is test", "I'm an event","What's Up?","Hello?","What's Up?","What's Up?","What's Up?"};
        Event.Type[] types = {Event.Type.PRIVATE,Event.Type.GROUP,Event.Type.PUBLIC,Event.Type.PUBLIC,Event.Type.PUBLIC,Event.Type.PUBLIC,Event.Type.PUBLIC};
        Event.Status[] statuses = { Event.Status.COMFIRM, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING};
        ArrayList<Event> events = new ArrayList<>();
        Date dt = new Date();
        dt.setTime(Calendar.getInstance().getTimeInMillis());
        long interval = 3600 * 1000;
        for (int i = 0; i < 7; i++) {
            Event event = new Event();
            event.setTitle(titles[i]);
            event.setStatus(statuses[i]);
            event.setEventType(types[i]);
            event.setStartTime(dt.getTime());
            event.setEndTime(dt.getTime() + (int)(interval));
            events.add(event);
        }

        return events;
    }
}
