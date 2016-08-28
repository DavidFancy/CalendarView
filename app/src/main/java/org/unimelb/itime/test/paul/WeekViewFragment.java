package org.unimelb.itime.test.paul;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Test;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.weekview.WeekView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Paul on 23/08/2016.
 */
public class WeekViewFragment extends Fragment implements WeekView.OnWeekViewChangeListener {
    private View root;
    private LayoutInflater inflater;
    private ViewGroup container;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null)
            root= inflater.inflate(R.layout.fragment_week_view,container,false);
        WeekView weekView = (WeekView) root.findViewById(R.id.week_view);
        // simulate Events
        ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH,31);
        calendar1.set(Calendar.HOUR_OF_DAY, 5);
        calendar1.set(Calendar.MINUTE,0);

        TestEvent testEvent = new TestEvent();
        testEvent.setTitle("itime meeting");
        testEvent.setStartTime(calendar1.getTimeInMillis());
        testEvent.setEndTime(calendar1.getTimeInMillis() + 3600000*2);
        testEvent.setEventType(1);
        testEvent.setStatus(5);
        eventArrayList.add(testEvent);
        weekView.setEvent(eventArrayList);



        weekView.setOnClickEventInterface(new WeekView.OnClickEventInterface() {
            @Override
            public void editEvent(ITimeEventInterface iTimeEventInterface) {
                Log.i("title",iTimeEventInterface.getTitle());
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.i("onStop","here here");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Log.i("onDestroy","here here");
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i("onResume","here here");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        weekViewPager.initAll();
//        Button button = (Button)root.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((PaulActivity)getActivity()).goToFragment2();
//            }
//        });
    }


    @Override
    public void onWeekChanged(Calendar calendar) {
        Log.d("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }
}