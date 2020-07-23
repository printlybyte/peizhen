package com.yinfeng.wypzh.http;


import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.message.MsgOrderNoticeParam;
import com.yinfeng.wypzh.bean.message.MsgOrderNoticeResult;
import com.yinfeng.wypzh.bean.order.CancelOrderParam;
import com.yinfeng.wypzh.bean.order.FreeVoucherListData;
import com.yinfeng.wypzh.bean.order.OnlineWaiterParam;
import com.yinfeng.wypzh.bean.order.OrderCancelReasonList;
import com.yinfeng.wypzh.bean.order.OrderCommentParam;
import com.yinfeng.wypzh.bean.order.OrderConfirmCompleteParam;
import com.yinfeng.wypzh.bean.order.OrderDetailBean;
import com.yinfeng.wypzh.bean.order.OrderListParam;
import com.yinfeng.wypzh.bean.order.OrderListResult;
import com.yinfeng.wypzh.bean.order.OrderStartServiceParam;
import com.yinfeng.wypzh.bean.order.RenewDetail;
import com.yinfeng.wypzh.bean.order.RenewParam;
import com.yinfeng.wypzh.bean.order.RenewResult;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailParam;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailResult;
import com.yinfeng.wypzh.bean.order.ServiceTimeBean;
import com.yinfeng.wypzh.bean.order.ShareServiceDetail;
import com.yinfeng.wypzh.bean.order.ShareVoucher;
import com.yinfeng.wypzh.bean.order.SubmitOrderParam;
import com.yinfeng.wypzh.bean.order.SubmitOrderResult;
import com.yinfeng.wypzh.http.common.RetrofitManager;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class OrderApi {
    private volatile static OrderApi instance;

    private OrderApi() {
    }

    public static OrderApi getInstance() {
        if (instance == null) {
            synchronized (OrderApi.class) {
                if (instance == null)
                    instance = new OrderApi();
            }
        }
        return instance;
    }

    /**
     * 获取当前可用免单券
     *
     * @return
     */
    public Observable<Response<BaseBean<FreeVoucherListData>>> getFreeVoucherList() {
        return RetrofitManager.getInstance().create(OrderService.class).getFreeVoucher();
    }

    /**
     * 获取服务项
     *
     * @param id
     * @return
     */
    public Observable<Response<ServiceOptionDetailResult>> getServiceOptionDetail(String id) {
        ServiceOptionDetailParam param = new ServiceOptionDetailParam(id);
        return RetrofitManager.getInstance().create(OrderService.class).getServiceOptionDetail(param);
    }

    /**
     * 获取陪诊服务时间
     *
     * @return
     */
    public Observable<Response<BaseBean<ServiceTimeBean>>> getServiceTime() {
        return RetrofitManager.getInstance().create(OrderService.class).getServiceTime();
    }

    /**
     * 提交订单
     *
     * @param param
     * @return
     */
    public Observable<Response<SubmitOrderResult>> submitOrder(SubmitOrderParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).submitOrder(param);
    }

    /**
     * 取消订单
     *
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<String>>> cancelOrder(CancelOrderParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).cancelOrder(param);
    }

    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    public Observable<Response<BaseBean<OrderDetailBean>>> getOrderDetail(String orderId) {
        return RetrofitManager.getInstance().create(OrderService.class).getOrderDetail(orderId);
    }

    /**
     * 获取订单列表
     *
     * @param pageNumb
     * @param pageSize
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<OrderListResult>>> getOrderList(int pageNumb, int pageSize, OrderListParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).getOrderList(pageNumb, pageSize, param);
    }

    /**
     * 获取服务详情的分享链接
     *
     * @return
     */
    public Observable<Response<BaseBean<ShareServiceDetail>>> getShareUrlServiceOption(String accountId) {
        return RetrofitManager.getInstance().create(OrderService.class).getShareUrlServiceOption(accountId);
    }

    /**
     * 获取服务完成后的分享链接
     *
     * @return
     */
    public Observable<Response<BaseBean<ShareVoucher>>> getShareUrlAfterComplete(String accountId) {
        return RetrofitManager.getInstance().create(OrderService.class).getShareUrlAfterComplete(accountId);
    }

    /**
     * 确认开始服务
     *
     * @return
     */
    public Observable<Response<BaseBean<String>>> startService(OrderStartServiceParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).startService(param);
    }

    /**
     * 确认服务完成
     *
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<String>>> confirmComplete(OrderConfirmCompleteParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).confirmComplete(param);
    }

    /**
     * submit order renew
     *
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<RenewResult>>> submitOrderRenew(RenewParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).submitOrderRenew(param);
    }

    /**
     * comment
     *
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<String>>> commentWaiter(OrderCommentParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).commentWaiter(param);
    }

    /**
     * 获取加单详情
     *
     * @param id
     * @return
     */
    public Observable<Response<BaseBean<RenewDetail>>> getRenewDetail(String id) {
        return RetrofitManager.getInstance().create(OrderService.class).getRenewDetail(id);
    }

    /**
     * 获取延时列表
     *
     * @return
     */
    public Observable<Response<BaseBean<List<RenewDetail>>>> getRenewList() {
        return RetrofitManager.getInstance().create(OrderService.class).getRenewList();
    }

    /**
     * 获取订单提醒列表
     *
     * @param pageNum
     * @param pageSize
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<MsgOrderNoticeResult>>> getMsgOrderNoticeList(int pageNum, int pageSize, MsgOrderNoticeParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).getMsgOrderNoticeList(pageNum, pageSize, param);
    }

    /**
     * 获取在线陪诊员数量
     *
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<String>>> getOnlineWaiterNum(OnlineWaiterParam param) {
        return RetrofitManager.getInstance().create(OrderService.class).getOnlineWaiterNum(param);
    }
    /**
     * 会员取消订单原因
     *
     * @return
     */
    public Observable<Response<BaseBean<OrderCancelReasonList>>> getCancelReasonList() {
        return RetrofitManager.getInstance().create(OrderService.class).getCancelReasonList();
    }

}
