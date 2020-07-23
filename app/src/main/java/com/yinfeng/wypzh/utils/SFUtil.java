package com.yinfeng.wypzh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.message.MessageOrderNotice;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailBean;

import java.util.ArrayList;
import java.util.List;

public class SFUtil {

    private static final String SFNAME_USERINFO = "sfname_userinfo";
    private static final String KEY_USERINFO_BEAN = "key_userinfo_bean";//UserInfo Bean

//    private static final String KEY_USERINFO_HEADLASTPATH = "userinfo_headlastpath";
//    private static final String KEY_USERINFO_TOKEN = "userinfo_token";
//    private static final String KEY_USERINFO_ISCOMPLETE = "userinfo_iscomplete";//是否已完善个人信息（必须）

    private static final String SFNAME_STATECONFIG = "sfname_stateconfig";
    private static final String KEY_STATECONFIG_MAINACTIVITY_CURRENT_POSITION = "mainactivity_currentposition";
    private static final String KEY_GLIDE_SIGNATURE_CACHEKEY_VERSION = "key_glide_signature_version";
    private static final String KEY_COMPLAIN_PHONE = "key_complain_phone";

    private static final String SFNAME_ORDER_LOOP = "sfname_order_loop";
    private static final String KEY_ORDER_LOOP_WAIT_RECEIVE = "key_order_loop_wait_receive";
    private static final String KEY_ORDER_LOOP_WAIT_SERVICE = "key_order_loop_wait_service";
    private static final String KEY_ORDER_LOOP_RENEW_REPLY = "key_order_loop_renew_reply";

    private static final String SFNAME_UMENGALIAS = "sfname_umengalias";

    private static final String SFNAME_MSG_NOTICE = "sfname_msgnotice";
    private static final String KEY_MSG_NOTICE_NEW = "key_msg_notice_new";
    private static final String KEY_MSG_NOTICE_OLD = "key_msg_notice_old";

    private static final String SFNAME_ALL_CONFIG = "sfname_all_config";
    private static final String KEY_IS_FIRST_OPEN = "key_isfirstopen";

    private static volatile SFUtil instance;

    public static SFUtil getInstance() {
        if (instance == null) {
            synchronized (SFUtil.class) {
                if (instance == null) {
                    instance = new SFUtil();
                }
            }
        }
        return instance;
    }

    private SFUtil() {

    }

    public SharedPreferences getAllConfig(Context context) {
        SharedPreferences sf = context.getSharedPreferences(SFNAME_ALL_CONFIG, Context.MODE_PRIVATE);
        return sf;
    }

    public void setIsFirstOpenYes(Context context) {
        SharedPreferences sf = getAllConfig(context);
        sf.edit().putBoolean(KEY_IS_FIRST_OPEN, false).apply();
    }

    public boolean getIsFirstOpen(Context context) {
        SharedPreferences sf = getAllConfig(context);
        return sf.getBoolean(KEY_IS_FIRST_OPEN, true);
    }

    public void clearAllConfigSf(Context context) {
        SharedPreferences sf = getAllConfig(context);
        sf.edit().clear().apply();
    }

    public SharedPreferences getMsgNotice(Context context) {
        SharedPreferences sf = context.getSharedPreferences(SFNAME_MSG_NOTICE, Context.MODE_PRIVATE);
        return sf;
    }

    public List<MessageOrderNotice> getMsgNoticeListNew(Context context) {
        List<MessageOrderNotice> list = null;
        SharedPreferences sf = getMsgNotice(context);
        String content = sf.getString(KEY_MSG_NOTICE_NEW, "");
        if (!TextUtils.isEmpty(content)) {
            try {
                list = new Gson().fromJson(content, new TypeToken<List<MessageOrderNotice>>() {
                }.getType());
            } catch (Exception e) {
            }

        }
        return list;
    }

    public List<MessageOrderNotice> getMsgNoticeListOld(Context context) {
        List<MessageOrderNotice> list = null;
        SharedPreferences sf = getMsgNotice(context);
        String content = sf.getString(KEY_MSG_NOTICE_OLD, "");
        if (!TextUtils.isEmpty(content)) {
            try {
                list = new Gson().fromJson(content, new TypeToken<List<MessageOrderNotice>>() {
                }.getType());
            } catch (Exception e) {
            }

        }
        return list;
    }

    public void addMsgNoticeListNew(Context context, List<MessageOrderNotice> newList) {
        if (newList != null && newList.size() > 0) {
            List<MessageOrderNotice> rawList = getMsgNoticeListNew(context);
            if (rawList == null)
                rawList = new ArrayList<>();
            rawList.addAll(0, newList);
            String content = new Gson().toJson(rawList);
            SharedPreferences sf = getMsgNotice(context);
            sf.edit().putString(KEY_MSG_NOTICE_NEW, content).apply();
        }
    }

    public void convertMsgNoticeNewToOld(Context context) {
        List<MessageOrderNotice> newList = getMsgNoticeListNew(context);
        if (newList != null && newList.size() > 0) {
            for (int i = 0; i < newList.size(); i++) {
                newList.get(i).setRead(true);
            }
            List<MessageOrderNotice> rawList = getMsgNoticeListOld(context);
            if (rawList == null)
                rawList = new ArrayList<>();
            rawList.addAll(0, newList);
            if (rawList.size() > 50) {
                rawList = rawList.subList(0, 50);
            }
            String content = new Gson().toJson(rawList);
            SharedPreferences sf = getMsgNotice(context);
            sf.edit().putString(KEY_MSG_NOTICE_OLD, content).apply();
            sf.edit().putString(KEY_MSG_NOTICE_NEW, "").apply();
        }
    }

    public void clearMsgNoticeSF(Context context) {
        getMsgNotice(context).edit().clear().apply();
    }

    public SharedPreferences getUserInfoSf(Context context) {
        SharedPreferences sf = context.getSharedPreferences(SFNAME_USERINFO, Context.MODE_PRIVATE);
        return sf;
    }

    public UserInfo getUserInfo(Context context) {
        SharedPreferences sf = getUserInfoSf(context);
        String userInfoStr = sf.getString(KEY_USERINFO_BEAN, "");

        if (TextUtils.isEmpty(userInfoStr))
            return new UserInfo();
        UserInfo info = new Gson().fromJson(userInfoStr, UserInfo.class);
        return info;
    }

    public void setUserInfo(Context context, UserInfo userInfo) {
        SharedPreferences sf = getUserInfoSf(context);
        Gson gson = new Gson();
        String userInfoJsonStr = gson.toJson(userInfo, UserInfo.class);
        sf.edit().putString(KEY_USERINFO_BEAN, userInfoJsonStr).apply();
    }

    public void putUserId(Context context, String userId) {
        UserInfo info = getUserInfo(context);
        info.setUserID(userId);
        setUserInfo(context, info);
    }

    public void putUserHeadLastImgPath(Context context, String imagePath) {
        UserInfo info = getUserInfo(context);
        info.setProfile(imagePath);
        setUserInfo(context, info);
    }


    public void putToken(Context context, String token) {
        UserInfo info = getUserInfo(context);
        info.setToken(token);
        setUserInfo(context, info);
    }

    public void putCompleteSate(Context context, boolean isComplete) {
        UserInfo info = getUserInfo(context);
        info.setComplete(isComplete);
        setUserInfo(context, info);
    }

    public String getUserID(Context context) {
        UserInfo info = getUserInfo(context);
        return info.getUserID();
    }

    public String getUserHeadLastImgPath(Context context) {
        UserInfo info = getUserInfo(context);
        return info.getProfile();
    }

    public String getToken(Context context) {
        UserInfo info = getUserInfo(context);
        return info.getToken();
    }

    public boolean getCompleteState(Context context) {
        UserInfo info = getUserInfo(context);
        return info.isComplete();
    }

    public SharedPreferences getUmengAliasSf(Context context) {
        //存储umeng alias
        SharedPreferences sf = context.getSharedPreferences(SFNAME_UMENGALIAS, Context.MODE_PRIVATE);
        return sf;
    }

    public void putDeviceTokenAndRelativeAlias(Context context, String deviceToken, String alias) {
        SharedPreferences sf = getUmengAliasSf(context);
        sf.edit().putString(deviceToken, alias).apply();
    }

    public String getAliasByDeviceToken(Context context, String deviceToken) {
        SharedPreferences sf = getUmengAliasSf(context);
        if (sf.contains(deviceToken)) {
            return sf.getString(deviceToken, null);
        }
        return null;
    }

    public void clearUmengAliasSF(Context context) {
        getUmengAliasSf(context).edit().clear().apply();
    }

    public SharedPreferences getStateConfigSf(Context context) {
        //存储一些需要缓存的状态变量
        SharedPreferences sf = context.getSharedPreferences(SFNAME_STATECONFIG, Context.MODE_PRIVATE);
        return sf;
    }

    public int getCurrentPostionMainActivity(Context context) {
        SharedPreferences sf = getStateConfigSf(context);
        return sf.getInt(KEY_STATECONFIG_MAINACTIVITY_CURRENT_POSITION, 0);
    }

    public long getGlideSignatureCacheVersion(Context context) {
        SharedPreferences sf = getStateConfigSf(context);
        return sf.getLong(KEY_GLIDE_SIGNATURE_CACHEKEY_VERSION, System.currentTimeMillis());
    }

    public void putCurrentPostionMainActivity(Context context, int currentPosition) {
        //内存被系统回收，再次进入，状态栏需要记录position来调整statusbar color
        SharedPreferences sf = getStateConfigSf(context);
        sf.edit().putInt(KEY_STATECONFIG_MAINACTIVITY_CURRENT_POSITION, currentPosition).apply();
    }

    public void setGlideSignatureCacheVersion(Context context, long currentTime) {
        //更改glide 缓存标记 用于图片刷新
        SharedPreferences sf = getStateConfigSf(context);
        sf.edit().putLong(KEY_GLIDE_SIGNATURE_CACHEKEY_VERSION, currentTime).apply();
    }

    /**
     * 存储投诉电话
     *
     * @param context
     * @param phone
     */
    public void putComplainPhone(Context context, String phone) {
        SharedPreferences sf = getStateConfigSf(context);
        sf.edit().putString(KEY_COMPLAIN_PHONE, phone).apply();
    }

    public String getComplainPhone(Context context) {
        SharedPreferences sf = getStateConfigSf(context);
        return sf.getString(KEY_COMPLAIN_PHONE, null);
    }

    /**
     * 订单等待服务 轮询列表
     *
     * @param context
     * @return
     */
    public SharedPreferences getOrderLoopSF(Context context) {
        SharedPreferences sf = context.getSharedPreferences(SFNAME_ORDER_LOOP, Context.MODE_PRIVATE);
        return sf;
    }

    /**
     * 添加订单id到 订单等待服务 轮询列表
     *
     * @param context
     * @param orderId
     */
    public void addOrderLoopWaitReceive(Context context, String orderId) {
        List<String> list = getOrderLoopListWaitReceive(context);
        if (list == null || list.size() == 0)
            list = new ArrayList<>();
        if (!list.contains(orderId))
            list.add(orderId);
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_RECEIVE, content).apply();

    }

    public void addOrderLoopWaitService(Context context, String orderId) {
        List<String> list = getOrderLoopListWaitService(context);
        if (list == null || list.size() == 0)
            list = new ArrayList<>();
        if (!list.contains(orderId))
            list.add(orderId);
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_SERVICE, content).apply();

    }


    public void addListOrderLoopWaitReceive(Context context, List<String> list) {
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_RECEIVE, content).apply();

    }

    public void addListOrderLoopWaitService(Context context, List<String> list) {
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_SERVICE, content).apply();

    }

    /**
     * 获取 订单等待服务 轮询列表
     *
     * @param context
     * @return
     */
    public List<String> getOrderLoopListWaitReceive(Context context) {
        SharedPreferences sf = getOrderLoopSF(context);
        String content = sf.getString(KEY_ORDER_LOOP_WAIT_RECEIVE, null);
        if (!TextUtils.isEmpty(content)) {
            List<String> list = new Gson().fromJson(content, new TypeToken<List<String>>() {
            }.getType());
            return list;
        }
        return null;
    }

    public List<String> getOrderLoopListWaitService(Context context) {
        SharedPreferences sf = getOrderLoopSF(context);
        String content = sf.getString(KEY_ORDER_LOOP_WAIT_SERVICE, null);
        if (!TextUtils.isEmpty(content)) {
            List<String> list = new Gson().fromJson(content, new TypeToken<List<String>>() {
            }.getType());
            return list;
        }
        return null;
    }

    public boolean isContainLoopWaitReceive(Context context, String orderId) {
        List<String> list = getOrderLoopListWaitReceive(context);
        if (!TextUtils.isEmpty(orderId) && list != null && list.contains(orderId))
            return true;
        return false;
    }

    /**
     * 待服务轮询列表移除订单id return true 代表列表为空 可以终止轮询
     *
     * @param context
     * @param orderId
     * @return true 轮询列表为空
     */
    public boolean removeLoopWaitReceive(Context context, String orderId) {
        List<String> list = getOrderLoopListWaitReceive(context);
        if (TextUtils.isEmpty(orderId))
            return false;
        if (list == null || list.size() == 0)
            return true;
        if (!list.contains(orderId))
            return false;
        list.remove(orderId);
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_RECEIVE, content).apply();
        if (list.size() == 0)
            return true;
        return false;
    }

    public boolean removeLoopWaitService(Context context, String orderId) {
        List<String> list = getOrderLoopListWaitService(context);
        if (TextUtils.isEmpty(orderId))
            return false;
        if (list == null || list.size() == 0)
            return true;
        if (!list.contains(orderId))
            return false;
        list.remove(orderId);
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_SERVICE, content).apply();
        if (list.size() == 0)
            return true;
        return false;
    }

    /**
     * 等待服务列表有数据吗 可以轮询吗
     *
     * @param context
     * @return
     */
    public boolean isCanLoopWaitReceive(Context context) {
        List<String> list = getOrderLoopListWaitReceive(context);
        if (list != null && list.size() > 0)
            return true;
        return false;
    }

    public void clearAllLoopWaitReceive(Context context) {
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_RECEIVE, "").apply();
    }

    public void clearAllLoopWaitService(Context context) {
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_WAIT_SERVICE, "").apply();
    }

    /**
     * 获取 延时回复 轮询列表
     *
     * @param context
     * @return
     */
    public List<String> getRenewLoopList(Context context) {
        SharedPreferences sf = getOrderLoopSF(context);
        String content = sf.getString(KEY_ORDER_LOOP_RENEW_REPLY, null);
        if (!TextUtils.isEmpty(content)) {
            List<String> list = new Gson().fromJson(content, new TypeToken<List<String>>() {
            }.getType());
            return list;
        }
        return null;
    }

    /**
     * 添加延时id到 延时回复 轮询列表
     *
     * @param context
     * @param renewId
     */
    public void addOrderLoopRenewReply(Context context, String renewId) {
        List<String> list = getRenewLoopList(context);
        if (list == null || list.size() == 0)
            list = new ArrayList<>();
        if (!list.contains(renewId))
            list.add(renewId);
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_RENEW_REPLY, content).apply();

    }

    public void addListOrderLoopRenew(Context context, List<String> list) {
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_RENEW_REPLY, content).apply();

    }

    /**
     * 延时回复轮询列表 移除延时id return true 代表列表为空 可以终止轮询
     *
     * @param context
     * @param renewId
     * @return true 轮询列表为空
     */
    public boolean removeLoopRenewReply(Context context, String renewId) {
        List<String> list = getRenewLoopList(context);
        if (TextUtils.isEmpty(renewId))
            return false;
        if (list == null || list.size() == 0)
            return true;
        if (!list.contains(renewId))
            return false;
        list.remove(renewId);
        String content = new Gson().toJson(list);
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_RENEW_REPLY, content).apply();
        if (list.size() == 0)
            return true;
        return false;
    }

    public void clearAllLoopRenewReply(Context context) {
        SharedPreferences sf = getOrderLoopSF(context);
        sf.edit().putString(KEY_ORDER_LOOP_RENEW_REPLY, "").apply();
    }

    public void clearUserInfoSF(Context context) {
        getUserInfoSf(context).edit().clear().apply();
    }

    public void clearStateConfigSF(Context context) {
        getStateConfigSf(context).edit().clear().apply();
    }

    public void clearOrderLoopSF(Context context) {
        getOrderLoopSF(context).edit().clear().apply();
    }

    public void clearAllSf(Context context) {
        clearUserInfoSF(context);
        clearStateConfigSF(context);
        clearOrderLoopSF(context);
        clearUmengAliasSF(context);
        clearMsgNoticeSF(context);
    }
}
