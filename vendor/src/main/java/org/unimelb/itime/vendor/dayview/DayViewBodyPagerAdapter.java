package org.unimelb.itime.vendor.dayview;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.List;

public class DayViewBodyPagerAdapter extends PagerAdapter {
    public String TAG = "MyAPP";

    private DayViewBodyController.OnCreateNewEvent onCreateNewEvent;
    private Calendar calendar = Calendar.getInstance();
    private OnBodyPageChanged onBodyPageChanged;
    private DayViewBodyController.BodyOnTouchListener bodyOnTouchListener;

    ArrayList<View> vLists;
    int upperBounds;

    public DayViewBodyPagerAdapter(ArrayList<View> vLists, int upperBounds) {
        this.vLists = vLists;
        this.upperBounds = upperBounds;
    }

    public void setOnCreateNewEvent(DayViewBodyController.OnCreateNewEvent onCreateNewEvent){
        this.onCreateNewEvent = onCreateNewEvent;
        for (int i = 0; i < vLists.size(); i++) {
            ((DayViewBody) vLists.get(i)).setOnCreateNewEvent(onCreateNewEvent);
        }
    }

    public void setBodyOnTouchListener(DayViewBodyController.BodyOnTouchListener bodyOnTouchListener) {
        this.bodyOnTouchListener = bodyOnTouchListener;
    }

    @Override
    public int getCount() {
        return upperBounds*2+1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DayViewBody v = (DayViewBody) vLists.get(position % vLists.size());
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null){
            parent.removeView(v);
        }
        v.resetView();
        v.getCalendar().setOffset(position - upperBounds - (calendar.get(Calendar.DAY_OF_WEEK)-1));
        if (this.onBodyPageChanged != null){
            Calendar calendar = v.getCalendar().getCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long beginOfDayMilliseconds = calendar.getTimeInMillis();
            List<ITimeEventInterface> events = this.onBodyPageChanged.loadEvents(beginOfDayMilliseconds);
            if (events != null){
                for (ITimeEventInterface event: events
                        ) {
                    v.addEvent(event);
                }
            }
        }else{
            Log.i(TAG, "instantiateItem: null listener");
        }
        if (this.onCreateNewEvent != null){
            v.setOnCreateNewEvent(this.onCreateNewEvent);
        }else {
            Log.i(TAG, "adapter:  onCreateNewEvent null");
        }
        if (this.bodyOnTouchListener != null){
            v.setBodyOnTouchListener(this.bodyOnTouchListener);
        }
        container.addView(v);

        return v;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(vLists.get(position % vLists.size()));
    }

    public void setOnBodyPageChanged(OnBodyPageChanged onBodyPageChanged){
        this.onBodyPageChanged = onBodyPageChanged;
    }

    public interface OnBodyPageChanged{
        List<ITimeEventInterface> loadEvents(long beginOfDayM);
    }
}