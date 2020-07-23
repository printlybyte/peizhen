package com.yinfeng.wypzh.utils;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Asen
 */
public class ContextUtilsTest {

    @Test
    public void main() {
        String content = ContextUtils.getPriceStrConvertFenToYuan(101);
        assertSame("￥1.01", content);
    }
}