package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import david.itime_calendar.R;
import david.itime_calendar.bean.Event;
import david.itimecalendar.calendar.listeners.ITimeCalendarMonthDayViewListener;
import david.itimecalendar.calendar.ui.monthview.DayViewBody;
import david.itimecalendar.calendar.ui.monthview.MonthView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

import static david.itime_calendar.MainActivity.eventManager;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class DayViewActivity extends AppCompatActivity {
    private static final String TAG = "DayViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayview);
        doTest();
    }

    private void doTest(){
        final MonthView monthView = (MonthView) findViewById(R.id.day_view);
        monthView.setITimeCalendarMonthDayViewListener(new ITimeCalendarMonthDayViewListener(){
            @Override
            public void onAllDayEventClick(ITimeEventInterface event) {

            }

            @Override
            public boolean isDraggable(DraggableEventView eventView) {
                return true;
            }

            @Override
            public void onEventCreate(DraggableEventView eventView) {
                Event event = new Event();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                String sStr = cal.getTime().toString();
                cal.setTimeInMillis(eventView.getEndTimeM());
                String eStr = cal.getTime().toString();
                Log.i("timetest", "start: " + sStr);
                Log.i("timetest", "end: " + eStr);
                event.setStartTime(eventView.getStartTimeM());
                event.setEndTime(eventView.getEndTimeM());
                eventManager.addEvent(event);
            }

            @Override
            public void onEventClick(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragStart(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DraggableEventView eventView) {
                ITimeEventInterface event = eventView.getEvent();
                BaseUtil.printEventTime("before",event);

                eventManager.updateEvent((Event) event, eventView.getStartTimeM(), eventView.getEndTimeM());

                BaseUtil.printEventTime("end",event);
                monthView.refresh();
            }

            @Override
            public void onDateChanged(Date date) {
                Log.i(TAG, "onDateChanged: " + date);
            }
        });
        monthView.setEventPackage(eventManager.getEventsMap());
    }
}
