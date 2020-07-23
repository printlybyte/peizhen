package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class WheelDialogNew extends BottomBaseDialog<WheelDialogNew> {
    public static final String SimpleFromatDATE_PATTERN = "yyyy-MM-dd HH:mm";
    private TextView tvCancel, tvConfirm, tvBirthday;
    private WheelView wheelViewDay;
    private WheelView wheelViewHour, wheelViewMin;
    ArrayWheelAdapter adapterDay;
    ArrayWheelAdapter adapterHour, adapterMinute;
    List<String> dayShowList, dayActualList;
    List<String> hourShowList, hourActualList, minuteShowList, minuteActualList;
    WheelTimeSelectListener mListener;

    SimpleDateFormat simpleDateFormat;
    // 8:30 -- 17:30 上班时间
    private int minHourToday = 8;
    private int minMinuteToday = 30;
    private int maxHourToday = 17;
    private int maxMinuteToday = 30;

    private String[] dayNames;

    private int hourNow, minuteNow;
    private int indexDay = 0;

    private String selectDayActual, selectHourActual, selectMinuteActual;
    private String selectHourShow, selectMinuteShow;
    private int minHour = 8;
    private int minMinute = 0;
    private String preTime;
    private String startTime, endTime;

    private int servicTimeDelt = 120;//minute
    //    private int advanceTimeDelt = 30;//minute
    private int advanceTimeDelt = 4 * 60;//minute
    private int actualForNum = -1;//组装日期 所需 fori 次数

    /**
     * @param context
     * @param servicTimeDelt  服务时长
     * @param advanceTimeDelt 提前时长
     * @param start           页面开始时间
     * @param end             页面结束时间
     * @param preTime
     * @param listener
     */
    public WheelDialogNew(Context context, int servicTimeDelt, int advanceTimeDelt, String start, String end, String preTime, WheelTimeSelectListener listener) {
        super(context);
        this.mListener = listener;
        this.advanceTimeDelt = advanceTimeDelt;
        dayNames = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        initHourAndMinuteNow();
        setStartAndEndTime(servicTimeDelt, start, end);
        this.preTime = preTime;
        mListener = listener;

    }

    /**
     * @param servicTimeDelt minute
     * @param start          07:00
     * @param end
     */
    private void setStartAndEndTime(int servicTimeDelt, String start, String end) {
        this.servicTimeDelt = servicTimeDelt;
        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
            try {
                Date dateStart = format.parse(start);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(dateStart);

                int hourStart = calendarStart.get(Calendar.HOUR_OF_DAY);
                int minuteStart = calendarStart.get(Calendar.MINUTE);
                minMinuteToday = minuteStart;
                minHourToday = hourStart;

                Date dateEnd = format.parse(end);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(dateEnd);
                calendarEnd.add(Calendar.MINUTE, -this.servicTimeDelt);

                int hourEnd = calendarEnd.get(Calendar.HOUR_OF_DAY);
                int minuteEnd = calendarEnd.get(Calendar.MINUTE);
                if (minuteEnd == 0) {
                    maxMinuteToday = 59;
                    maxHourToday = hourEnd - 1;
                } else {
                    maxMinuteToday = minuteEnd;
                    maxHourToday = hourEnd;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }


    private void initHourAndMinuteNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, advanceTimeDelt);
        hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        minuteNow = calendar.get(Calendar.MINUTE);
    }

    @Override
    public View onCreateView() {
        LogUtil.error("onCreateView");
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_wheel_new, null);
        tvCancel = view.findViewById(R.id.cancel);
        tvConfirm = view.findViewById(R.id.confirm);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        wheelViewDay = view.findViewById(R.id.wheelviewday);
        wheelViewHour = view.findViewById(R.id.wheelviewhour);
        wheelViewMin = view.findViewById(R.id.wheelviewmin);
        initWheelView();
        return view;
    }

    @Override
    public void setUiBeforShow() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismiss();
                checkTime();
//                if (mListener != null)
//                    mListener.selectedTime(startTime, endTime);
            }
        });

    }

    private void checkTime() {
        //12点-下午1点半 医院上班时间提示
        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            try {
                Date dateStart = format.parse(startTime);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(dateStart);

                int hourStart = calendarStart.get(Calendar.HOUR_OF_DAY);
                int minuteStart = calendarStart.get(Calendar.MINUTE);

                Date dateEnd = format.parse(endTime);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(dateEnd);

                int hourEnd = calendarEnd.get(Calendar.HOUR_OF_DAY);
                int minuteEnd = calendarEnd.get(Calendar.MINUTE);

                boolean isStartNeedTip = checkTimeContain(hourStart, minuteStart);
                boolean isEndNeedTip = checkTimeContain(hourEnd, minuteEnd);
                if (isStartNeedTip || isEndNeedTip) {
                    showTipDialog();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        dismiss();
        if (mListener != null) {
            mListener.selectedTime(startTime, endTime);
        }
    }

    private void showTipDialog() {
        String content = "医院12:00-13:30是下班时间，确定继续？";
        final MaterialDialog dialog = DialogHelper.getMaterialDialogQuick(mContext, content);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();

                    }
                }
                ,
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        dismiss();
                        if (mListener != null)
                            mListener.selectedTime(startTime, endTime);
                    }
                }
        );
        dialog.show();
    }

    private boolean checkTimeContain(int hourStart, int minuteStart) {
        if (hourStart == 12)
            return true;
        if (hourStart == 13) {
            if (minuteStart <= 30)
                return true;
        }
        return false;
    }

    private boolean judgeTodayShow() {
//        if (hourNow + 2 > maxHourToday)
        if (hourNow > maxHourToday)
            return false;
//        if (hourNow + 2 == maxHourToday && minuteNow > maxMinuteToday)
        if (hourNow == maxHourToday && minuteNow > maxMinuteToday)
            return false;
        return true;
    }

    private boolean isValidWeekDay(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0)
            dayOfWeek = 0;
        if (dayOfWeek == 0 || dayOfWeek == 6)
            return false;
        return true;
    }

    private List<String> getOptionItemDayActualList(boolean isNeedShowToday) {
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);


        for (int i = 0; i < actualForNum; i++) {
            String dayStr = formater.format(calendar.getTime());
            if (i == 0) {
                if (isNeedShowToday && isValidWeekDay(calendar)) {
                    list.add(dayStr);
                }
            } else {
                if (isValidWeekDay(calendar)) {
                    list.add(dayStr);
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

//        String todayStr = formater.format(calendar.getTime());
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        String secondStr = formater.format(calendar.getTime());
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        String threeStr = formater.format(calendar.getTime());
//        if (isNeedShowToday)
//            list.add(todayStr);
//        list.add(secondStr);
//        list.add(threeStr);
        return list;
    }

    private String getWeekDayName(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0)
            dayOfWeek = 0;
        return dayNames[dayOfWeek];
    }

    private List<String> getOptionItemDayShowList(boolean isNeedShowToday) {
        List<String> list = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < actualForNum; i++) {
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dateStr = month + "月" + day + "日  " + getWeekDayName(calendar);

            if (i == 0) {
                if (isNeedShowToday && isValidWeekDay(calendar)) {
                    dateStr = month + "月" + day + "日  今 天";
                    list.add(dateStr);
                }
            } else {
                if (isValidWeekDay(calendar)) {
                    list.add(dateStr);
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }


//        int monthOne = calendar.get(Calendar.MONTH) + 1;
//        int dayOne = calendar.get(Calendar.DAY_OF_MONTH);
////        int weekOne = calendar.get(Calendar.DAY_OF_WEEK);
//        String oneStr = monthOne + "月" + dayOne + "日  今 天";
//
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        int monthTwo = calendar.get(Calendar.MONTH) + 1;
//        int dayTwo = calendar.get(Calendar.DAY_OF_MONTH);
////        int weekTwo = calendar.get(Calendar.DAY_OF_WEEK);
//        String twoStr = monthTwo + "月" + dayTwo + "日  " + getWeekDayName(calendar);
//
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        int monthThree = calendar.get(Calendar.MONTH) + 1;
//        int dayThree = calendar.get(Calendar.DAY_OF_MONTH);
//        String threeStr = monthThree + "月" + dayThree + "日  " + getWeekDayName(calendar);

//        if (isNeedShowToday)
//            list.add(oneStr);
//        list.add(twoStr);
//        list.add(threeStr);
        return list;
    }

    private List<String> getOptionItemsHourActualList(boolean isToday) {
        List<String> items = new ArrayList<>();
        minHour = minHourToday;
        if (isToday) {
//            minHour = Math.max(hourNow + 2, minHourToday);
            minHour = Math.max(hourNow, minHourToday);
        }
        LogUtil.error(" getOptionItemsHourActualList minHour " + minHour);
        for (int i = minHour; i <= maxHourToday; i++) {
            if (i < 10) {
                items.add("0" + i);
            } else {
                items.add(String.valueOf(i));
            }
        }
        return items;
    }

    private List<String> getOptionItemsHourShowList(boolean isToday) {
        List<String> items = new ArrayList<>();
        minHour = minHourToday;
        if (isToday) {
//            minHour = Math.max(hourNow + 2, minHourToday);
            minHour = Math.max(hourNow, minHourToday);
        }
        for (int i = minHour; i <= maxHourToday; i++) {
            items.add(i + "点");
        }
        return items;
    }

    private List<String> getOptionItemsMinuteActualList(boolean isToday) {
        List<String> items = new ArrayList<>();
        minMinute = minMinuteToday;
        int maxMinute = 59;

        if (isToday) {
//            if (hourNow + 2 < minHourToday) {
            if (hourNow < minHourToday) {
                minMinute = minMinuteToday;
            }
//            if (hourNow + 2 > minHourToday && hourNow + 2 < maxHourToday) {
            if (hourNow > minHourToday && hourNow < maxHourToday) {
                minMinute = minuteNow;
            }
//            if (hourNow + 2 == minHourToday) {
            if (hourNow == minHourToday) {
                minMinute = Math.max(minuteNow, minMinuteToday);
            }
//            if (hourNow + 2 == maxHourToday) {
            if (hourNow == maxHourToday) {
                minMinute = minuteNow;
                maxMinute = maxMinuteToday;
            }
        } else {
            minMinute = minMinuteToday;
        }

        for (int i = minMinute; i <= maxMinute; i++) {
            if (i < 10) {
                items.add("0" + i);
            } else {
                items.add(String.valueOf(i));
            }
        }
        return items;

    }

    private List<String> getOptionItemsMinuteShowList(boolean isToday) {
        List<String> items = new ArrayList<>();
        minMinute = minMinuteToday;
        int maxMinute = 59;

        if (isToday) {
//            if (hourNow + 2 < minHourToday) {
            if (hourNow < minHourToday) {
                minMinute = minMinuteToday;
            }
//            if (hourNow + 2 > minHourToday && hourNow + 2 < maxHourToday) {
            if (hourNow > minHourToday && hourNow < maxHourToday) {
                minMinute = minuteNow;
            }
//            if (hourNow + 2 == minHourToday) {
            if (hourNow == minHourToday) {
                minMinute = Math.max(minuteNow, minMinuteToday);
            }
//            if (hourNow + 2 == maxHourToday) {
            if (hourNow == maxHourToday) {
                minMinute = minuteNow;
                maxMinute = maxMinuteToday;
            }
        } else {
            minMinute = minMinuteToday;
        }

        for (int i = minMinute; i <= maxMinute; i++) {
            items.add(i + "分");
        }
        return items;
    }

    private int getActualForNum(boolean isNeedShowToday) {
        int forNumb = 5;
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0)
            dayOfWeek = 0;
        switch (dayOfWeek) {
            case 0://今天是周日
                forNumb = 6;
                break;
            case 1:
                if (isNeedShowToday)
                    forNumb = 5;
                else
                    forNumb = 7;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                if (isNeedShowToday)
                    forNumb = 7;
                else
                    forNumb = 8;
                break;
            case 6:
                forNumb = 7;
                break;

        }

        return forNumb;
    }

    private void initWheelView() {
        simpleDateFormat = new SimpleDateFormat(SimpleFromatDATE_PATTERN, Locale.CHINA);

        //1.当前时间是否超过干细胞上班时间范围，超过则不显示今天。
        boolean isNeedShowToday = judgeTodayShow();
        actualForNum = getActualForNum(isNeedShowToday);
        dayActualList = getOptionItemDayActualList(isNeedShowToday);
        dayShowList = getOptionItemDayShowList(isNeedShowToday);

        boolean isToday = dayActualList.size() == 3 && indexDay == 0;

        hourActualList = getOptionItemsHourActualList(isToday);
        hourShowList = getOptionItemsHourShowList(isToday);

        minuteActualList = getOptionItemsMinuteActualList(isToday);
        minuteShowList = getOptionItemsMinuteShowList(isToday);

        initBaseWheelView(wheelViewDay);
        initBaseWheelView(wheelViewHour);
        initBaseWheelView(wheelViewMin);

        //初始化 日
        adapterDay = new ArrayWheelAdapter(dayShowList);
        wheelViewDay.setAdapter(adapterDay);
        indexDay = 0;
        selectDayActual = dayActualList.get(0);

        wheelViewDay.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                LogUtil.error("indexDay : " + indexDay);
                LogUtil.error("minHour : " + minHour);
                indexDay = index;
                selectDayActual = dayActualList.get(index);
                resetHour();
                resetMinute();
                resetBirthDay();
            }
        });

        //初始化 小时
        adapterHour = new ArrayWheelAdapter(hourShowList);
        wheelViewHour.setAdapter(adapterHour);

        selectHourActual = hourActualList.get(0);
        selectHourShow = hourShowList.get(0);
        wheelViewHour.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                LogUtil.error("onItemSelected  day  index :" + index);
                selectHourActual = hourActualList.get(index);
                selectHourShow = hourShowList.get(index);
                resetMinute();
                resetBirthDay();
            }
        });

        //初始化 分钟
        adapterMinute = new ArrayWheelAdapter(minuteShowList);
        wheelViewMin.setAdapter(adapterMinute);
        selectMinuteActual = minuteActualList.get(0);
        selectMinuteShow = minuteShowList.get(0);
        wheelViewMin.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                selectMinuteActual = minuteActualList.get(index);
                selectMinuteShow = minuteShowList.get(index);
                resetBirthDay();
            }
        });
        initPreTime();
        resetBirthDay();
    }

    private void resetBirthDay() {
        startTime = selectDayActual + " " + selectHourActual + ":" + selectMinuteActual;
        tvBirthday.setText(startTime);
        try {
            Date date = simpleDateFormat.parse(startTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, servicTimeDelt);
            endTime = simpleDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initPreTime() {
        if (!TextUtils.isEmpty(preTime)) {
            String preDay = preTime.substring(0, "yyyy-MM-dd".length());
            int indexDayPre = dayActualList.indexOf(preDay);
            if (indexDayPre != -1) {
                wheelViewDay.setCurrentItem(indexDayPre);
                indexDay = indexDayPre;
                selectDayActual = preDay;
                resetHour();

                String preHour = preTime.substring("yyyy-MM-dd".length() + 1, "yyyy-MM-dd".length() + 3);
                int indexHourPre = hourActualList.indexOf(preHour);
                if (indexHourPre != -1) {
                    wheelViewHour.setCurrentItem(indexHourPre);
                    selectHourActual = preHour;
                    selectHourShow = hourShowList.get(indexHourPre);
                    resetMinute();

                    String minutePre = preTime.substring(SimpleFromatDATE_PATTERN.length() - 2, SimpleFromatDATE_PATTERN.length());
                    int indexMinutePre = minuteActualList.indexOf(minutePre);
                    if (indexMinutePre != -1) {
                        wheelViewMin.setCurrentItem(indexMinutePre);
                        selectMinuteActual = minutePre;
                        selectMinuteShow = minuteShowList.get(indexMinutePre);
                    }

                }

            }
        }
    }


    private int getMinuteByStr(String minuteStr) {
        return Integer.parseInt(minuteStr.substring(0, minuteStr.length() - 1));
    }

    private int getHourByStr(String hourStr) {
        return Integer.parseInt(hourStr.substring(0, hourStr.length() - 1));
    }

    private boolean isToday() {
        return dayActualList.size() == 3 && indexDay == 0;
    }

    private void resetHour() {
        boolean isToday = isToday();
        List<String> showList = resetHourShow(isToday);
        List<String> actualList = resetHourActual(isToday);

        adapterHour = new ArrayWheelAdapter(showList);
        wheelViewHour.setAdapter(adapterHour);
        hourShowList = showList;
        hourActualList = actualList;

        int hour = getHourByStr(selectHourShow);
        int hourFirst = getHourByStr(hourShowList.get(0));
        int hourLast = getHourByStr(hourShowList.get(hourShowList.size() - 1));

        if (hour <= hourFirst) {
            wheelViewHour.setCurrentItem(0);
            selectHourActual = hourActualList.get(0);
            selectHourShow = hourShowList.get(0);
        }
        if (hour >= hourLast) {
            wheelViewHour.setCurrentItem(hourShowList.size() - 1);
            selectHourActual = hourActualList.get(hourActualList.size() - 1);
            selectHourShow = hourShowList.get(hourShowList.size() - 1);
        }
        if (hour > hourFirst && hour < hourLast) {
            int index = hourShowList.indexOf(selectHourShow);
            wheelViewHour.setCurrentItem(index);
        }
    }

    private List<String> resetHourActual(boolean isToday) {
        int min = minHourToday;
        int max = maxHourToday;
        if (isToday) {
            min = minHour;
        }
        List<String> items = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            if (i < 10) {
                items.add("0" + i);
            } else {
                items.add(String.valueOf(i));
            }
        }
        return items;

    }

    private List<String> resetHourShow(boolean isToday) {
        int min = minHourToday;
        int max = maxHourToday;
        if (isToday) {
            min = minHour;
        }
        List<String> items = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            items.add(i + "点");
        }
        return items;
    }

    private void resetMinute() {

        boolean isToday = isToday();
        List<String> showList = resetMinuteShowList(isToday);
        List<String> actualList = resetMinuteActualList(isToday);

        adapterMinute = new ArrayWheelAdapter(showList);
        wheelViewMin.setAdapter(adapterMinute);
        minuteShowList = showList;
        minuteActualList = actualList;

        int minute = getMinuteByStr(selectMinuteShow);
        int minuteFirst = getMinuteByStr(minuteShowList.get(0));
        int minuteLast = getMinuteByStr(minuteShowList.get(minuteShowList.size() - 1));

        if (minute <= minuteFirst) {
            wheelViewMin.setCurrentItem(0);
            selectMinuteActual = minuteActualList.get(0);
            selectMinuteShow = minuteShowList.get(0);
        }
        if (minute >= minuteLast) {
            wheelViewMin.setCurrentItem(minuteShowList.size() - 1);
            selectMinuteActual = minuteActualList.get(minuteActualList.size() - 1);
            selectMinuteShow = minuteShowList.get(minuteShowList.size() - 1);
        }
        if (minute > minuteFirst && minute < minuteLast) {
            int index = minuteShowList.indexOf(selectMinuteShow);
            wheelViewMin.setCurrentItem(index);
        }

    }

    private List<String> resetMinuteShowList(boolean isToday) {
        List<String> items = new ArrayList<>();
        int min = 0;
        int max = 59;

        int selectHour = getHourByStr(selectHourShow);
        if (isToday) {
            if (selectHour == minHour) {
                min = minMinute;
            }
            if (selectHour == maxHourToday) {
                max = maxMinuteToday;
            }
        } else {
            if (selectHour == minHourToday) {
                min = minMinuteToday;
                max = 59;
            }
            if (selectHour == maxHourToday) {
                min = 0;
                max = maxMinuteToday;
            }
        }
        for (int i = min; i <= max; i++) {
            items.add(i + "分");
        }
        return items;
    }

    private List<String> resetMinuteActualList(boolean isToday) {
        List<String> items = new ArrayList<>();
        int min = 0;
        int max = 59;

        int selectHour = getHourByStr(selectHourShow);
        if (isToday) {
            if (selectHour == minHour) {
                min = minMinute;
            }
            if (selectHour == maxHourToday) {
                max = maxMinuteToday;
            }
        } else {
            if (selectHour == minHourToday) {
                min = minMinuteToday;
                max = 59;
            }
            if (selectHour == maxHourToday) {
                min = 0;
                max = maxMinuteToday;
            }
        }
        for (int i = min; i <= max; i++) {
            if (i < 10) {
                items.add("0" + i);
            } else {
                items.add(String.valueOf(i));
            }
        }
        return items;
    }

    private void initBaseWheelView(WheelView wheelView) {
        wheelView.setCyclic(false);
        wheelView.setDividerType(WheelView.DividerType.FILL);
    }


    public interface WheelTimeSelectListener {
        void selectedTime(String date, String dateEnd);
    }
}
