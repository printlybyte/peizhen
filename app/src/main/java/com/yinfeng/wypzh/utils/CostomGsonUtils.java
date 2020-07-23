package com.yinfeng.wypzh.utils;

import com.google.gson.Gson;

/**
 * ============================================
 * 描  述：
 * 包  名：com.yinfeng.wypzh.utils
 * 类  名：CostomGsonUtils
 * 创建人：liuguodong
 * 创建时间：2019/8/26 14:21
 * ============================================
 **/
public class CostomGsonUtils<T> {
    T t1;

    public CostomGsonUtils(T tx) {
        t1=tx;
    }

    public T getBean(String string, Class<?> classs) {
        try {
            t1 = (T) new Gson().fromJson(string, classs);
            return t1;
        } catch (Exception e) {
            return null;
        }
    }


}
