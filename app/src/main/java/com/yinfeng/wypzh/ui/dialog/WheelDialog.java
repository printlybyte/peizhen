package com.yinfeng.wypzh.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.utils.ContextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WheelDialog extends BottomBaseDialog<WheelDialog> {
    public static final String SimpleFromatDATE_PATTERN = "yyyy-MM-dd HH:mm";
    private TextView tvCancel, tvConfirm, tvBirthday;
    private WheelView wheelViewYear, wheelViewMonth, wheelViewDay;
    private WheelView wheelViewHour, wheelViewMin;
    int year, month, day, hour, minute;
    int yearMin, monthMin, dayMin, hourMin, minuteMin;
    String selectdTime;
    int currentTimeDaysMaxNum;
    ArrayWheelAdapter adapterMonth, adapterDay, adapterYear;
    ArrayWheelAdapter adapterHour, adapterMinute;
    List<String> mothShowList, mothActualList, dayList, yearList;
    List<String> hourShowList, hourActualList, minuteShowList, minuteActualList;
    WheelTimeSelectListener mListener;
    private String preTime;
    private Calendar preCalendar;
    private int serviceTime = 120;

    public WheelDialog(Context context, String time, WheelTimeSelectListener listener) {
        super(context);
        this.mListener = listener;
        preCalendar = Calendar.getInstance();
        preCalendar.add(Calendar.HOUR_OF_DAY, 2);
        setPreTime(time);
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    private void setPreTime(String preTime) {
        this.preTime = preTime;
        if (TextUtils.isEmpty(preTime))
            return;
        SimpleDateFormat formater = new SimpleDateFormat(SimpleFromatDATE_PATTERN);
        try {
            Date date = formater.parse(this.preTime);
            preCalendar = Calendar.getInstance();
            preCalendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_wheel, null);
        tvCancel = view.findViewById(R.id.cancel);
        tvConfirm = view.findViewById(R.id.confirm);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        wheelViewYear = view.findViewById(R.id.wheelviewyear);
        wheelViewMonth = view.findViewById(R.id.wheelviewmonth);
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
                if (mListener != null) {
                    String selectTime = getTimeStr(year, month, day, hour, minute);
                    String selectTimeAdd = getTimeStrAdd(year, month, day, hour, minute);
                    mListener.selectedTime(selectTime, selectTimeAdd);
                }
                dismiss();
            }
        });

    }

    private void initWheelView() {
        mothShowList = getOptionItemsMothshowList(monthMin);
        mothActualList = getOptionItemsMothActualList(monthMin);
        dayList = getOptionItemDays(dayMin, currentTimeDaysMaxNum);
        yearList = getOptionItemYears(yearMin, yearMin + 10);
        hourShowList = getOptionItemsHourShowList(hourMin);
        hourActualList = getOptionItemsHourActualList(hourMin);
        minuteShowList = getOptionItemsMinuteShowList(minuteMin);
        minuteActualList = getOptionItemsMinuteActualList(minuteMin);

        initBaseWheelView(wheelViewMonth);
        initBaseWheelView(wheelViewDay);
        initBaseWheelView(wheelViewYear);
        initBaseWheelView(wheelViewHour);
        initBaseWheelView(wheelViewMin);

        //初始化 年
        adapterYear = new ArrayWheelAdapter(yearList);
        wheelViewYear.setAdapter(adapterYear);
        int indexYear = yearList.indexOf(String.valueOf(year));
        wheelViewYear.setCurrentItem(indexYear);
        wheelViewYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String content = yearList.get(index);
                year = Integer.parseInt(content);
//                calculateMaxDayNumAndResetDayWheel();
                resetAll();
            }
        });
        //初始化 日
        adapterDay = new ArrayWheelAdapter(dayList);
        wheelViewDay.setAdapter(adapterDay);
        int indexDay = dayList.indexOf(String.valueOf(day));
        wheelViewDay.setCurrentItem(indexDay);
        wheelViewDay.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String content = dayList.get(index);
                day = Integer.parseInt(content);
                resetHourWheelViewMin();
                resetMinWheelViewMin();
                resetTvBirthday();
            }
        });
        //初始化 月
        adapterMonth = new ArrayWheelAdapter(mothShowList);
        int indexMonth = mothActualList.indexOf(String.valueOf(month));
        wheelViewMonth.setAdapter(adapterMonth);
        wheelViewMonth.setCurrentItem(indexMonth);
        wheelViewMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String content = mothActualList.get(index);
                month = Integer.parseInt(content);
//                calculateMaxDayNumAndResetDayWheel();
                resetAll();
            }
        });

        //初始化 小时
        adapterHour = new ArrayWheelAdapter(hourShowList);
        wheelViewHour.setAdapter(adapterHour);
        int indexHour = hourActualList.indexOf(String.valueOf(hour));
        wheelViewHour.setCurrentItem(indexHour);
        wheelViewHour.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String content = hourActualList.get(index);
                hour = Integer.parseInt(content);
                resetMinWheelViewMin();
                resetTvBirthday();
            }
        });

        //初始化 分钟
        adapterMinute = new ArrayWheelAdapter(minuteShowList);
        wheelViewMin.setAdapter(adapterMinute);
        int indexMinute = minuteActualList.indexOf(String.valueOf(minute));
        wheelViewMin.setCurrentItem(indexMinute);
        wheelViewMin.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String content = minuteActualList.get(index);
                minute = Integer.parseInt(content);
                resetTvBirthday();
            }
        });

        initCurrentYearMonthDay(preCalendar);
    }

    private void initCurrentYearMonthDay(Calendar calendar) {
        SimpleDateFormat formater = new SimpleDateFormat(SimpleFromatDATE_PATTERN);
        selectdTime = formater.format(calendar.getTime());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        currentTimeDaysMaxNum = ContextUtils.getMaxDaysNum(selectdTime);
        resetTvBirthday();
        refreshMinTime();
        resetAll();
    }

    private void refreshMinTime() {
//        SimpleDateFormat formater = new SimpleDateFormat(SimpleFromatDATE_PATTERN);
        Calendar minCalandar = Calendar.getInstance();
        minCalandar.add(Calendar.HOUR_OF_DAY, 2);
        yearMin = minCalandar.get(Calendar.YEAR);
        monthMin = minCalandar.get(Calendar.MONTH) + 1;
        dayMin = minCalandar.get(Calendar.DAY_OF_MONTH);
        hourMin = minCalandar.get(Calendar.HOUR_OF_DAY);
        minuteMin = minCalandar.get(Calendar.MINUTE);
    }

    /**
     * @param start 必须是1-11
     * @return
     */
    private List<String> getOptionItemsMothshowList(int start) {
        List<String> list = new ArrayList<>();
        list.add("一月");
        list.add("二月");
        list.add("三月");
        list.add("四月");
        list.add("五月");
        list.add("六月");
        list.add("七月");
        list.add("八月");
        list.add("九月");
        list.add("十月");
        list.add("十一月");
        list.add("十二月");
        if (start - 1 > 0) {
            for (int i = 0; i < start - 1; i++) {
                list.remove(0);
            }
        }
        return list;
    }

    /**
     * 月份的实际数据
     *
     * @param start 包含
     * @return
     */
    private List<String> getOptionItemsMothActualList(int start) {
        List<String> items = new ArrayList<>();
        for (int i = start; i <= 12; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }


    /**
     * @param start 0-22
     * @return
     */
    private List<String> getOptionItemsHourActualList(int start) {
        List<String> items = new ArrayList<>();
        for (int i = start; i <= 23; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }

    private List<String> getOptionItemsHourShowList(int start) {
        List<String> items = new ArrayList<>();
        items.add("00时");
        items.add("01时");
        items.add("02时");
        items.add("03时");
        items.add("04时");
        items.add("05时");
        items.add("06时");
        items.add("07时");
        items.add("08时");
        items.add("09时");
        for (int i = 10; i < 24; i++) {
            items.add(i + "时");
        }
        if (start > 0) {
            for (int i = 0; i < start; i++) {
                items.remove(0);
            }
        }
        return items;
    }

    private List<String> getOptionItemsMinuteShowList(int start) {
        List<String> items = new ArrayList<>();
        items.add("00分");
        items.add("01分");
        items.add("02分");
        items.add("03分");
        items.add("04分");
        items.add("05分");
        items.add("06分");
        items.add("07分");
        items.add("08分");
        items.add("09分");
        for (int i = 10; i < 60; i++) {
            items.add(i + "分");
        }
        if (start > 0) {
            for (int i = 0; i < start; i++) {
                items.remove(0);
            }
        }
        return items;
    }

    /**
     * @param start 0-59
     * @return
     */
    private List<String> getOptionItemsMinuteActualList(int start) {
        List<String> items = new ArrayList<>();
        for (int i = start; i < 60; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }

    private List<String> getOptionItemDays(int start, int max) {
        List<String> items = new ArrayList<>();
        for (int i = start; i <= max; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }

    private List<String> getOptionItemYears(int start, int end) {
        List<String> items = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }

    private void initBaseWheelView(WheelView wheelView) {
        wheelView.setCyclic(false);
        wheelView.setDividerType(WheelView.DividerType.FILL);
    }

    private synchronized void calculateMaxDayNumAndResetDayWheel() {
        String time = getTimeStr(year, month, 1, 0, 0);
        int maxDaysNum = ContextUtils.getMaxDaysNum(time);
        resetDaysWheelView(maxDaysNum);
        resetTvBirthday();
    }

    private synchronized void resetAll() {
        String time = getTimeStr(year, month, 1, 0, 0);
        int maxDaysNum = ContextUtils.getMaxDaysNum(time);
        resetMonthWheelViewMin();
        resetDaysWheelViewMin(maxDaysNum);
        resetHourWheelViewMin();
        resetMinWheelViewMin();
        resetTvBirthday();
    }

    private void resetMinWheelViewMin() {
        if (year == yearMin && month == monthMin && day == dayMin && hour == hourMin) {
            minuteActualList = getOptionItemsMinuteActualList(minuteMin);
            minuteShowList = getOptionItemsMinuteShowList(minuteMin);
            adapterMinute = new ArrayWheelAdapter(minuteShowList);
            wheelViewMin.setAdapter(adapterMinute);
            if (minute < minuteMin) {
                minute = minuteMin;
                wheelViewMin.setCurrentItem(0);
            } else {
                int index = minuteActualList.indexOf(String.valueOf(minute));
                wheelViewMin.setCurrentItem(index);
            }
        } else {
            minuteActualList = getOptionItemsMinuteActualList(0);
            minuteShowList = getOptionItemsMinuteShowList(0);
            adapterMinute = new ArrayWheelAdapter(minuteShowList);
            wheelViewMin.setAdapter(adapterMinute);
            int index = minuteActualList.indexOf(String.valueOf(minute));
            wheelViewMin.setCurrentItem(index);
        }
    }

    private void resetHourWheelViewMin() {
        if (year == yearMin && month == monthMin && day == dayMin) {
            hourActualList = getOptionItemsHourActualList(hourMin);
            hourShowList = getOptionItemsHourShowList(hourMin);
            adapterHour = new ArrayWheelAdapter(hourShowList);
            wheelViewHour.setAdapter(adapterHour);
            if (hour < hourMin) {
                hour = hourMin;
                wheelViewHour.setCurrentItem(0);
            } else {
                int index = hourActualList.indexOf(String.valueOf(hour));
                wheelViewHour.setCurrentItem(index);
            }
        } else {
            hourActualList = getOptionItemsHourActualList(0);
            hourShowList = getOptionItemsHourShowList(0);
            adapterHour = new ArrayWheelAdapter(hourShowList);
            wheelViewHour.setAdapter(adapterHour);
            int index = hourActualList.indexOf(String.valueOf(hour));
            wheelViewHour.setCurrentItem(index);
        }
    }

    private void resetMonthWheelViewMin() {
        if (year == yearMin) {
            mothActualList = getOptionItemsMothActualList(monthMin);
            mothShowList = getOptionItemsMothshowList(monthMin);
            adapterMonth = new ArrayWheelAdapter(mothShowList);
            wheelViewMonth.setAdapter(adapterMonth);
            if (month < monthMin) {
                month = monthMin;
                wheelViewMonth.setCurrentItem(0);
            } else {
                int index = mothActualList.indexOf(String.valueOf(month));
                wheelViewMonth.setCurrentItem(index);
            }
        } else {
            mothActualList = getOptionItemsMothActualList(1);
            mothShowList = getOptionItemsMothshowList(1);
            adapterMonth = new ArrayWheelAdapter(mothShowList);
            wheelViewMonth.setAdapter(adapterMonth);
            int index = mothActualList.indexOf(String.valueOf(month));
            wheelViewMonth.setCurrentItem(index);
        }
    }

    private void resetDaysWheelViewMin(int maxNum) {
//        if (maxNum == currentTimeDaysMaxNum && month != monthMin)
//            return;
        if (year == yearMin && monthMin == month) {
            dayList = getOptionItemDays(dayMin, maxNum);
            adapterDay = new ArrayWheelAdapter(dayList);
            wheelViewDay.setAdapter(adapterDay);

            if (maxNum < day) {
                wheelViewDay.setCurrentItem(dayList.size() - 1);
                day = maxNum;
            } else {
                if (day < dayMin) {
                    day = dayMin;
                    wheelViewDay.setCurrentItem(0);
                } else {
                    wheelViewDay.setCurrentItem(dayList.indexOf(String.valueOf(day)));
                }
            }

        } else {
            dayList = getOptionItemDays(1, maxNum);
            adapterDay = new ArrayWheelAdapter(dayList);
            wheelViewDay.setAdapter(adapterDay);

            if (maxNum < day) {
                wheelViewDay.setCurrentItem(maxNum - 1);
                day = maxNum;
            } else {
                wheelViewDay.setCurrentItem(dayList.indexOf(String.valueOf(day)));
            }
        }

//        currentTimeDaysMaxNum = maxNum;
    }

    private void resetTvBirthday() {
        String birthday = getTimeStr(year, month, day, hour, minute);
        tvBirthday.setText(birthday);
    }


    private void resetDaysWheelView(int maxNum) {
        if (maxNum == currentTimeDaysMaxNum)
            return;
        dayList = getOptionItemDays(1, maxNum);
        adapterDay = new ArrayWheelAdapter(dayList);
        wheelViewDay.setAdapter(adapterDay);
        if (maxNum < day) {
            wheelViewDay.setCurrentItem(maxNum - 1);
            day = maxNum;
        } else {
            wheelViewDay.setCurrentItem(dayList.indexOf(String.valueOf(day)));
        }
        currentTimeDaysMaxNum = maxNum;
    }

    private String getTimeStr(int year, int month, int day, int hour, int minute) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(year + "年");
//        sb.append(month + "月");
//        sb.append(day + "日");
//        sb.append(" " + day + "时");
//        sb.append(day + "分");
//        return sb.toString();
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, day, hour, minute);
        return ContextUtils.convertDateToString(temp.getTime());
    }

    private String getTimeStrAdd(int year, int month, int day, int hour, int minute) {
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, day, hour, minute);
        temp.add(Calendar.MINUTE, serviceTime);
        return ContextUtils.convertDateToString(temp.getTime());
    }

    public interface WheelTimeSelectListener {
        void selectedTime(String date, String dateEnd);
    }
}
