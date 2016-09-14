package org.unimelb.itime.vendor.weekview;

import android.content.ClipData;
import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yinchuandong on 22/08/2016.
 */

public class WeekViewBody extends LinearLayout{
    private int totalHeight = 0;
    private int totalWidth = 0;
    private int hourHeight = 0;
    private int hourWidth = 0;
    private int dayHeight = 0;
    private int dayWidth = 0;
    private int oneWeekWidth = 0;
    private int numOfHourShowInScreen = 8;
    private int dottedLineHeight = 0;

    private TreeMap<Integer,String> timeSlotTreeMap = new TreeMap<>();
    private TreeMap<Integer, String> daySlotTreeMap = new TreeMap<>();

    private ScrollView scrollView;
    private RelativeLayout backGroundRelativeLayout;
    private LinearLayout weekBodyLinearLayout;
    private RelativeLayout timeRelativeLayout;
    private RelativeLayout eventWidgetsRelativeLayout;
    private RelativeLayout eventAndWidgetsRelativeLayout;
    private RelativeLayout eventRelativeLayout;
    private TextView msgWindow;
    private TextView currentTimeView;
    private MyDragListener myDragListener;
    private MyCalendar myCalendar;
    Calendar calendar = Calendar.getInstance();
    private ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();
    private ArrayList<WeekDraggableEventView> eventViewArrayList = new ArrayList<>();

    private WeekView.OnClickEventInterface onClickEventInterface;

    private TextView[] hourTextViewArr = new TextView[getHours().length];
    private TextView[] timeLineTextViewArr = new TextView[getHours().length];
    private TextView currentTimeLineTextView;
    private TextView currentTimeTextView;

    private OnWeekBodyListener onWeekBodyListener;

    private float tapX;
    private float tapY;

    public WeekViewBody(Context context) {
        super(context);
        init();
    }

    public WeekViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
//        myCalendar = new MyCalendar(calendar);
        initWidgets();
        initHourTextViews();
        initTimeDottedLine();
        initMsgWindow();

//        // for drag events
//        initTimeSlot(getHours());
//        initDaySlot();
        initDragListener();
    }

    private void initWidgets() {
        // init scrollView
        this.removeAllViews();
        scrollView = new ScrollView(getContext());
        // init background relativeLayout
        backGroundRelativeLayout = new RelativeLayout(getContext());
        // init linearLayout which will contain timeRelativeLayout and eventRelativeLayout
        weekBodyLinearLayout = new LinearLayout(getContext());
        weekBodyLinearLayout.setOrientation(HORIZONTAL);

        // init timeRelativeLayout
        timeRelativeLayout = new RelativeLayout(getContext());

        eventWidgetsRelativeLayout = new RelativeLayout(getContext());
        eventWidgetsRelativeLayout.setPadding(20,0,0,0);

        eventRelativeLayout = new RelativeLayout(getContext());
        eventRelativeLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tapX = event.getX();
                tapY = event.getY();
                return false;
            }
        });
        eventRelativeLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //create event here;

                return false;
            }
        });
        eventAndWidgetsRelativeLayout = new RelativeLayout(getContext());

        eventAndWidgetsRelativeLayout.addView(eventWidgetsRelativeLayout);
        eventAndWidgetsRelativeLayout.addView(eventRelativeLayout);
        weekBodyLinearLayout.addView(timeRelativeLayout);
        weekBodyLinearLayout.addView(eventAndWidgetsRelativeLayout);
        backGroundRelativeLayout.addView(weekBodyLinearLayout);
        scrollView.addView(backGroundRelativeLayout);
        this.addView(scrollView);
    }

    public void initHourTextViews() {
        for (int i = 0; i < getHours().length; i++) {
            hourTextViewArr[i] = new TextView(getContext());
            hourTextViewArr[i].setText(getHours()[i]);
            hourTextViewArr[i].setTextSize(12);
            timeRelativeLayout.addView(hourTextViewArr[i]);
        }
    }

    public void initTimeDottedLine() {
        for (int i = 0; i < getHours().length; i++) {
            timeLineTextViewArr[i] = new TextView(getContext());
            timeLineTextViewArr[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_dotted_line));
            timeLineTextViewArr[i].setLayerType(timeLineTextViewArr[i].LAYER_TYPE_SOFTWARE, null);
            timeLineTextViewArr[i].setGravity(Gravity.CENTER);
            eventWidgetsRelativeLayout.addView(timeLineTextViewArr[i]);
        }
    }

    private void initCurrentTimeLine() {
        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());
        if (isShowingToday(myCalendar, todayCalendar)) {
            // set the current time line
            currentTimeLineTextView = new TextView(getContext());
            currentTimeLineTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.current_time_red_line));
//            currentTimeLineTextView.setBackgroundColor(Color.RED);
            backGroundRelativeLayout.addView(currentTimeLineTextView);

            // set the showing time
            int currentHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = todayCalendar.get(Calendar.MINUTE);
            currentTimeTextView = new TextView(getContext());
            String stringCurrentHour = currentHour < 10 ? "0" + String.valueOf(currentHour) : String.valueOf(currentHour);
            String stringCurrentMinute = currentMinute < 10 ? "0" + String.valueOf(currentMinute) : String.valueOf(currentMinute);
            String AMPM = currentHour > 12 ? "PM" : "AM";
            currentTimeTextView.setText(String.format("%s:%s %s", stringCurrentHour, stringCurrentMinute, AMPM));
            currentTimeTextView.setTextSize(8);
            currentTimeTextView.setTextColor(Color.RED);
            backGroundRelativeLayout.addView(currentTimeTextView);
        }
    }

    public void initMsgWindow() {
        msgWindow = new TextView(getContext());
        msgWindow.setTextSize(20);
        msgWindow.setText("msgWindow");
        msgWindow.setVisibility(View.INVISIBLE);
        eventWidgetsRelativeLayout.addView(msgWindow);
    }

    private void initTimeSlot(String[] hours){
        timeSlotTreeMap.clear();
        double startPoint = hourHeight /2;
        double timeSlotHeight = hourHeight /4;
        for(int time = 0; time < hours.length; time++){
            timeSlotTreeMap.put((int)startPoint + hourHeight * time, hours[time]);
            String hourPart = hours[time].substring(0,2);
            for (int quarterSlot = 0 ; quarterSlot < 3; quarterSlot ++){
                String minute = String.valueOf((quarterSlot+1)*15);
                String thisTime = hourPart + ":" + minute;
                int PositionY = (int)(startPoint + hourHeight * time + timeSlotHeight * (quarterSlot +1));
                timeSlotTreeMap.put(PositionY,thisTime);
            }
        }
    }

    private void initDaySlot(){
        double startPoint =0;
        double daySlotWidth = dayWidth;
        String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        for (int slot = 0; slot <7; slot++ ){
            int positionX = (int)startPoint + (int)daySlotWidth * slot;
            daySlotTreeMap.put( positionX ,days[slot]);
        }
    }

    public boolean isShowingToday(MyCalendar myCalendar, Calendar todayCalendar) {
        for (int i = 0; i < 7; i++) {
            if (myCalendar.getDay() == todayCalendar.get(Calendar.DATE) &&
                    myCalendar.getMonth() == todayCalendar.get(Calendar.MONTH) &&
                    myCalendar.getYear() == todayCalendar.get(Calendar.YEAR)) {
                return true;
            } else {
                todayCalendar.set(Calendar.DATE, todayCalendar.get(Calendar.DATE) - 1);
            }
        }
        return false;
    }

    public void initDragListener(){
        if (myDragListener == null){
            myDragListener = new MyDragListener();
            eventRelativeLayout.setOnDragListener(myDragListener);
        }else{
            eventRelativeLayout.setOnDragListener(myDragListener);
        }
    }

    public void initEvents(){
        if (eventArrayList!=null){
            //first remove contains
            eventRelativeLayout.removeAllViews();
            for (ITimeEventInterface event: eventArrayList){
                Date eventDate = new Date(event.getStartTime());
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(eventDate);
                if (isInCurrentWeek(eventCalendar,myCalendar)) {
                    WeekDraggableEventView eventView = new WeekDraggableEventView(getContext(),event);
                    eventView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onWeekBodyListener != null){
                                onWeekBodyListener.onEventClick((WeekDraggableEventView) v);
                            }
                        }
                    });
//                    eventView.setOnLongClickListener(new MyLongClickListener()); // for event draggable
                    eventViewArrayList.add(eventView);
                    eventRelativeLayout.addView(eventView);
                }
            }
        }
    }

    public void updateEvents(){
        if (eventViewArrayList!=null){
            for (final WeekDraggableEventView eventView:eventViewArrayList){
                long startTime = eventView.getEvent().getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int topOffset = hourHeight * hour + hourHeight * minute/60 + dottedLineHeight/2;
                int leftOffSet = dayWidth * (day-1);
                int duration = (int) ((eventView.getEvent().getEndTime() - eventView.getEvent().getStartTime())/1000/60);
                int eventHeight = duration *hourHeight / 60;
                eventView.layout(leftOffSet, topOffset, leftOffSet + dayWidth, topOffset + eventHeight);
                eventView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickEventInterface.onClickEditEvent(eventView.getEvent());
                    }
                });
            }
        }
    }

    public void setEvents(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
        initEvents();
        updateEvents();
        requestLayout();
        invalidate();
    }

    public boolean isInCurrentWeek(Calendar timeSlotCalendar,MyCalendar myCalendar){
        Calendar firstSundayCalendar = Calendar.getInstance();
        firstSundayCalendar.set(Calendar.YEAR, myCalendar.getYear());
        firstSundayCalendar.set(Calendar.MONTH, myCalendar.getMonth());
        firstSundayCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getDay());
        return (timeSlotCalendar.get(Calendar.WEEK_OF_YEAR) == firstSundayCalendar.get(Calendar.WEEK_OF_YEAR)
                && timeSlotCalendar.get(Calendar.YEAR) == firstSundayCalendar.get(Calendar.YEAR));
    }

    public void setOnClickEventInterface(WeekView.OnClickEventInterface onClickEventInterface){
        this.onClickEventInterface = onClickEventInterface;
    }

    private String[] getHours(){
        String[] HOURS = new String[]{
                "00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00",
                "08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00",
                "16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00",
                "24:00"
        };
        return  HOURS;
    }

    public void setNumOfHourShowInScreen(int number){
        this.numOfHourShowInScreen = number;
    }

//    *******************************************************************

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(totalWidth,totalHeight);
        updateWidthHeight(totalWidth,totalHeight);

        for (int hour = 0;hour < getHours().length ; hour++){
            // set hour view
            MLayoutParams hourParams = new MLayoutParams(hourWidth,hourHeight);
            hourParams.top = hourHeight * hour;
            hourParams.left = 0;
            hourTextViewArr[hour].setPadding(0,55,0,0); // gravity center has problem
            hourTextViewArr[hour].setLayoutParams(hourParams);

            // set dotted line
            MLayoutParams dottedParams = new MLayoutParams(oneWeekWidth, dayHeight);
            dottedParams.top = dayHeight * hour;
            dottedParams.left = 0;
            timeLineTextViewArr[hour].setGravity(Gravity.CENTER);
            timeLineTextViewArr[hour].setLayoutParams(dottedParams);
        }

        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());
        if (isShowingToday(myCalendar, todayCalendar)) {
            int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = todayCalendar.get(Calendar.MINUTE);

            MLayoutParams currentTimeLineParams = new MLayoutParams(oneWeekWidth,dayHeight);
            currentTimeLineParams.top = hour * hourHeight + minute * hourHeight / 60;
            currentTimeLineParams.left = hourWidth;
            currentTimeLineTextView.setGravity(Gravity.CENTER);
            currentTimeLineTextView.setLayoutParams(currentTimeLineParams);

            MLayoutParams currentTimeTextParams = new MLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, hourHeight);
            currentTimeTextParams.left =0;
            currentTimeTextParams.top = hour * hourHeight + minute * hourHeight / 60;
            currentTimeTextView.setPadding(0,55,0,0);
            currentTimeTextView.setLayoutParams(currentTimeTextParams);
        }





    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        updateWidthHeight(totalWidth, totalHeight);
        scrollView.layout(0, 0, totalWidth, totalHeight);
        backGroundRelativeLayout.layout(0, 0, totalWidth, hourHeight * getHours().length);
        weekBodyLinearLayout.layout(0, 0, totalWidth, hourHeight * getHours().length);
        eventAndWidgetsRelativeLayout.layout(hourWidth, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);
        timeRelativeLayout.layout(0, 0, hourWidth, hourHeight * getHours().length);
        eventWidgetsRelativeLayout.layout(0, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);
        eventRelativeLayout.layout(0, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);

        // set 00:00, 01:00 ...
        for (int hour = 0; hour < getHours().length; hour++) {
            MLayoutParams hourParams = (MLayoutParams) hourTextViewArr[hour].getLayoutParams();
            hourTextViewArr[hour].layout(hourParams.left, hourParams.top, hourParams.left + hourWidth, hourParams.top + hourHeight );
        }
        // set dotted line
        for (int hour = 0; hour < getHours().length; hour++) {
            MLayoutParams timeLineParams = (MLayoutParams) timeLineTextViewArr[hour].getLayoutParams();
            timeLineTextViewArr[hour].layout(timeLineParams.left, timeLineParams.top, timeLineParams.left+oneWeekWidth, timeLineParams.top+ dayHeight);
        }

        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());

        if (isShowingToday(myCalendar, todayCalendar)) {
            MLayoutParams currentTimeLineParams = (MLayoutParams) currentTimeLineTextView.getLayoutParams();
            currentTimeLineTextView.layout(
                    currentTimeLineParams.left, currentTimeLineParams.top, currentTimeLineParams.left +
                            oneWeekWidth, currentTimeLineParams.top+ hourHeight);

            MLayoutParams currentTimeTextParams = (MLayoutParams) currentTimeTextView.getLayoutParams();
            currentTimeTextView.layout(currentTimeTextParams.left, currentTimeTextParams.top,
                    currentTimeTextParams.left + hourWidth, currentTimeTextParams.top+hourHeight);
        }


        // init events
        if (eventViewArrayList!=null){
            for (final WeekDraggableEventView eventView:eventViewArrayList){
                long startTime = eventView.getEvent().getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int topOffset = hourHeight * hour + hourHeight * minute/60 + hourHeight/2;
                int leftOffSet = dayWidth * (day-1);
                int duration = (int) ((eventView.getEvent().getEndTime() - eventView.getEvent().getStartTime())/1000/60);
                int eventHeight = duration *hourHeight / 60;
                eventView.layout(leftOffSet, topOffset, leftOffSet + dayWidth, topOffset + eventHeight);
                eventView.setOnLongClickListener(new MyLongClickListener()); // for draggable

                eventView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickEventInterface.onClickEditEvent(eventView.getEvent());
                    }
                });
            }
        }
        // for drag events
        initTimeSlot(getHours());
        initDaySlot();


    }

    public void updateWidthHeight(int totalWidth, int totalHeight) {
        this.hourHeight = totalHeight / numOfHourShowInScreen;
        this.hourWidth = totalWidth / 10;
        this.dayHeight = hourHeight;
        this.dayWidth = (int) (totalWidth * 0.9) / 7;
        this.oneWeekWidth = (int) (totalWidth * 0.9);
        this.dottedLineHeight = hourHeight/3;
    }

    public MyCalendar getMyCalendar() {
        return myCalendar;
    }

    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
        if (backGroundRelativeLayout.getChildCount()>1){
            backGroundRelativeLayout.removeAllViews();
            backGroundRelativeLayout.addView(weekBodyLinearLayout);
        }
        initCurrentTimeLine();
    }

    public WeekView.OnClickEventInterface getOnCLickEventInterface() {
        return onClickEventInterface;
    }

    public static class MLayoutParams extends RelativeLayout.LayoutParams {

        public int left = 0;
        public int top = 0;

        public MLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public MLayoutParams(int width, int height) {
            super(width, height);
        }
    }

//    ********************************************************************

    private final class MyLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.VISIBLE);
            view.getBackground().setAlpha(255);
            return false;
        }
    }


    private final class MyDragListener implements View.OnDragListener{
        float actionStartX = 0;
        float actionStartY = 0;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            WeekDraggableEventView currentEventView = (WeekDraggableEventView) dragEvent.getLocalState();
            switch (dragEvent.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:

                    actionStartX = dragEvent.getX();
                    actionStartY = dragEvent.getY();
                    msgWindow.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    scrollViewAutoScroll(dragEvent);
                    msgWindowFollow((int)dragEvent.getX(),(int)dragEvent.getY(),(View)dragEvent.getLocalState());
                    if (onWeekBodyListener != null){
                        onWeekBodyListener.onEventDragging(currentEventView, (int) dragEvent.getX(), (int) dragEvent.getY());
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    //need set currentEventView.event time, calendar
//                    String new_time = positionToTimeTreeMap.get(reComputeResult[1]);
//                    //important! update event time after drag
//                    String[] time_parts = new_time.split(":");
//                    currentEventNewHour = Integer.valueOf(time_parts[0]);
//                    currentEventNewMinutes = Integer.valueOf(time_parts[1]);
                    if (onWeekBodyListener != null){
                        onWeekBodyListener.onEventDragging(currentEventView, (int) dragEvent.getX(), (int) dragEvent.getY());
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    View finalView = (View) dragEvent.getLocalState();
                    finalView.getBackground().setAlpha(150);
                    msgWindow.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private  int[] reComputePositionToSet(int actualX, int actualY, View draggableObj, View container){
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();

        int objWidth = draggableObj.getWidth();
        int objHeight = draggableObj.getHeight();

        int finalX = actualX;
        int finalY = actualY;

        if (actualX < 0){
            finalX = 0;
        }else if (actualX + objWidth > containerWidth){
            finalX = containerWidth - objWidth;
        }

        if (actualY < 0){
            finalY = 0;
        }else if(actualY + objHeight > containerHeight){
            finalY = containerHeight - objHeight;
        }
        int findNearestPositionY = nearestTimeSlotKey(finalY);
        int findNearestPositionX = nearestDaySlotKey(finalX);
        if (findNearestPositionY != -1){
            finalY = findNearestPositionY;
        }else{
            Log.d("TAG", "reComputePositionToSet: "+ "Error no such position");

        }

        if (findNearestPositionX != -1){
            finalX = findNearestPositionX;
        }else{
            Log.d("TAG", "reComputePositionToSet: "+ "Error no such position");
        }
        return new int[] {finalX, finalY};
    }

    private int nearestTimeSlotKey(int tapY){
        int key = tapY;
        Map.Entry<Integer, String> low = timeSlotTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = timeSlotTreeMap.ceilingEntry(key);
        if (low != null && high != null){
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        }else if (low != null || high != null){
            return low != null ? low.getKey() : high.getKey();
        }
        return -1;
    }

    private int nearestDaySlotKey(int tapX){
        int key = tapX;
        Map.Entry<Integer, String> low = daySlotTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = daySlotTreeMap.ceilingEntry(key);
        if (low != null && high != null){
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        }else if(low != null || high != null){
            return low != null? low.getKey() : high.getKey();
        }
        return -1;
    }

    public void scrollViewAutoScroll(DragEvent event){
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);
        float heightOfView = ((View)event.getLocalState()).getHeight();
        float needPositionY_top = event.getY() - heightOfView/2;
        float needPositionY_bottom = event.getY() + heightOfView/2;

        if (scrollBounds.top > needPositionY_top){
            int offsetY = (int)(scrollView.getScrollY() + needPositionY_top - scrollBounds.top);
            scrollView.scrollTo(scrollView.getScrollX(), offsetY);
        } else if(scrollBounds.bottom < needPositionY_bottom){
            int offsetY = (int)(scrollView.getScrollY() + needPositionY_bottom - scrollBounds.bottom);
            scrollView.scrollTo(scrollView.getScrollX(), offsetY);
        }
    }

    private void msgWindowFollow(int tapX, int tapY, View followView){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)msgWindow.getLayoutParams();
        params.topMargin = tapY - followView.getHeight()/2 - msgWindow.getHeight();

        if(tapX + msgWindow.getWidth()/2 > eventRelativeLayout.getWidth()){
            params.leftMargin = eventRelativeLayout.getWidth() - msgWindow.getWidth();
        }else if(tapX - msgWindow.getWidth()/2 < 0){
            params.leftMargin = 0;
        }else{
            params.leftMargin = tapX - msgWindow.getWidth()/2;
        }
        int nearestProperPosition = nearestTimeSlotKey(tapY - followView.getHeight()/2);
        int nearestProperDay = nearestDaySlotKey(tapX - followView.getWidth()/2);
        if (nearestProperPosition != -1 && nearestProperDay != -1){
//            Log.d("day",daySlotTreeMap.get(nearestProperDay));
//            Log.d("time",timeSlotTreeMap.get(nearestProperPosition));
            msgWindow.setVisibility(View.VISIBLE);
            msgWindow.setText(daySlotTreeMap.get(nearestProperDay) + " "+ timeSlotTreeMap.get(nearestProperPosition) );

        }else{
            Log.d("TAG","msgWindowFollow: "+ "Error, text not found in Map");
        }
        msgWindow.setLayoutParams(params);
        //msgWindow.setVisibility(View.VISIBLE);
    }

    public interface OnWeekBodyListener{
        void onEventCreate(WeekDraggableEventView eventView);
        void onEventClick(WeekDraggableEventView eventView);
        void onEventDragStart(WeekDraggableEventView eventView);
        void onEventDragging(WeekDraggableEventView eventView, int x, int y);
        void onEventDragDrop(WeekDraggableEventView eventView);
    }

    public void setOnWeekBodyListener(OnWeekBodyListener onWeekBodyListener){
        this.onWeekBodyListener = onWeekBodyListener;
    }
}
