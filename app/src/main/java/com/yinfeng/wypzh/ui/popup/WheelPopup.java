package com.yinfeng.wypzh.ui.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.ui.dialog.ArrayWheelAdapter;
import com.yinfeng.wypzh.utils.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WheelPopup extends BasePopup<WheelPopup> {
    private TextView tvCancel, tvConfirm, tvBirthday;
    private WheelView wheelViewYear, wheelViewMonth, wheelViewDay;
    private WheelView wheelViewHour, wheelViewMin;
    int year, month, day, hour, minute;
    String selectdTime;
    int currentTimeDaysMaxNum;
    ArrayWheelAdapter adapterMonth, adapterDay, adapterYear;
    ArrayWheelAdapter adapterHour, adapterMinute;
    List<String> mothShowList, mothActualList, dayList, yearList;
    List<String> hourShowList, hourActualList, minuteShowList, minuteActualList;

    public WheelPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreatePopupView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_wheel, null);
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

    }

    private void initWheelView() {
        initCurrentYearMonthDay();
        mothShowList = getOptionItemsMothshowList();
        mothActualList = getOptionItemsMothActualList();
        dayList = getOptionItemDays(currentTimeDaysMaxNum);
        yearList = getOptionItemYears(1990, year);
        hourShowList = getOptionItemsHourActualList();
        hourShowList = getOptionItemsHourShowList();
        hourActualList = getOptionItemsHourActualList();
        minuteShowList = getOptionItemsMinuteShowList();
        minuteActualList = getOptionItemsMinuteActualList();

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
                calculateMaxDayNumAndResetDayWheel();
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
                calculateMaxDayNumAndResetDayWheel();
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
                resetTvBirthday();
            }
        });

        //初始化 分钟
        adapterMinute = new ArrayWheelAdapter(minuteShowList);
        wheelViewMin.setAdapter(adapterDay);
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

    }

    private void initCurrentYearMonthDay() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Calendar calendar = Calendar.getInstance();
        selectdTime = formater.format(calendar.getTime());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        currentTimeDaysMaxNum = ContextUtils.getMaxDaysNum(selectdTime);

    }

    private List<String> getOptionItemsMothshowList() {
        List<String> mOptionsItems0 = new ArrayList<>();
        mOptionsItems0.add("一月");
        mOptionsItems0.add("二月");
        mOptionsItems0.add("三月");
        mOptionsItems0.add("四月");
        mOptionsItems0.add("五月");
        mOptionsItems0.add("六月");
        mOptionsItems0.add("七月");
        mOptionsItems0.add("八月");
        mOptionsItems0.add("九月");
        mOptionsItems0.add("十月");
        mOptionsItems0.add("十一月");
        mOptionsItems0.add("十二月");
        return mOptionsItems0;
    }

    private List<String> getOptionItemsMothActualList() {
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }


    private List<String> getOptionItemsHourActualList() {
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }

    private List<String> getOptionItemsHourShowList() {
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
        return items;
    }

    private List<String> getOptionItemsMinuteShowList() {
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
        return items;
    }

    private List<String> getOptionItemsMinuteActualList() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            items.add(String.valueOf(i));
        }
        return items;
    }

    private List<String> getOptionItemDays(int max) {
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= max; i++) {
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
        wheelView.setCyclic(true);
        wheelView.setDividerType(WheelView.DividerType.FILL);
    }

    private synchronized void calculateMaxDayNumAndResetDayWheel() {
        String time = getTimeStr(year, month, 1, 0, 0);
        int maxDaysNum = ContextUtils.getMaxDaysNum(time);
        resetDaysWheelView(maxDaysNum);
        resetTvBirthday();
    }

    private void resetTvBirthday() {
        String birthday = getTimeStr(year, month, day, hour, minute);
        tvBirthday.setText(birthday);
    }

    private void resetDaysWheelView(int maxNum) {
        if (maxNum == currentTimeDaysMaxNum)
            return;
        dayList = getOptionItemDays(maxNum);
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
        temp.set(year, month, day, hour, minute);
        return ContextUtils.convertDateToString(temp.getTime());
    }

}
