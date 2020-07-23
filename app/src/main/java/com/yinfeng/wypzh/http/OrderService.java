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
import com.yinfeng.wypzh.bean.patient.HospitalBodyParam;
import com.yinfeng.wypzh.bean.patient.HospitalListData;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.yinfeng.wypzh.http.common.ApiContents.BASE_URL;

public interface OrderService {
//    /**
//     * 删除 就诊人
//     *
//     * @return
//     */
//
//    @POST(BASE_URL + "memberPatient/delete")
//    @FormUrlEncoded
//    Observable<BaseBean> deletePatient(@Field("ids") String ids);

//    /**
//     * 修改就诊人
//     *
//     * @param editParam
//     * @return
//     */
//    @POST(BASE_URL + "memberPatient/save")
//    Observable<BaseBean> editPatient(@Body PatientEditParam editParam);

//    /**
//     * 搜索医院
//     *
//     * @param pageNum
//     * @param pageSize
//     * @param name
//     * @return
//     */
//    @POST(BASE_URL + "hospital/queryPage")
//    Observable<HospitalListResult> searchHospitalByName(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Body HospitalBodyParam name);

    /**
     * 获取可用免单全
     *
     * @return
     */
    @POST(BASE_URL + "couponAccount/queryAll")
    Observable<Response<BaseBean<FreeVoucherListData>>> getFreeVoucher();

    /**
     * 获取服务详情
     *
     * @param idBody
     * @return
     */
    @POST(BASE_URL + "product/getOne")
    Observable<Response<ServiceOptionDetailResult>> getServiceOptionDetail(@Body ServiceOptionDetailParam idBody);

    /**
     * 获取陪诊服务时间
     *
     * @return
     */
    @POST(BASE_URL + "common/serviceTime")
    Observable<Response<BaseBean<ServiceTimeBean>>> getServiceTime();


    /**
     * 提交订单
     *
     * @param param
     * @return
     */
    @POST(BASE_URL + "order/submit")
    Observable<Response<SubmitOrderResult>> submitOrder(@Body SubmitOrderParam param);

    /**
     * @param param
     * @return
     */
    @POST(BASE_URL + "order/cancel")
    Observable<Response<BaseBean<String>>> cancelOrder(@Body CancelOrderParam param);


    /**
     * 获取订单详情
     *
     * @param id
     * @return
     */
    @POST(BASE_URL + "order/{id}")
    Observable<Response<BaseBean<OrderDetailBean>>> getOrderDetail(@Path("id") String id);

    /**
     * 获取订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param orderListParam
     * @return
     */
    @POST(BASE_URL + "order/member/list")
    Observable<Response<BaseBean<OrderListResult>>> getOrderList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Body OrderListParam orderListParam);

    /**
     * 获取服务详情的分享链接
     *
     * @return
     */
    @POST(BASE_URL + "common/proShare")
    Observable<Response<BaseBean<ShareServiceDetail>>> getShareUrlServiceOption(@Query("accountId") String accountId);

    /**
     * 获取服务完成后的分享链接
     *
     * @return
     */
    @POST(BASE_URL + "common/serviceShare")
    Observable<Response<BaseBean<ShareVoucher>>> getShareUrlAfterComplete(@Query("accountId") String accountId);

    /**
     * 确认开始服务
     *
     * @return
     */
    @POST(BASE_URL + "order/service")
    Observable<Response<BaseBean<String>>> startService(@Body OrderStartServiceParam param);

    /**
     * 确认服务完成
     *
     * @param param
     * @return
     */
    @POST(BASE_URL + "order/member/complete")
    Observable<Response<BaseBean<String>>> confirmComplete(@Body OrderConfirmCompleteParam param);

    /**
     * submit renew order
     *
     * @param param
     * @return
     */
    @POST(BASE_URL + "order/overtime/submit")
    Observable<Response<BaseBean<RenewResult>>> submitOrderRenew(@Body RenewParam param);


    /**
     * comment
     *
     * @param param
     * @return
     */
    @POST(BASE_URL + "waiterEva/eva")
    Observable<Response<BaseBean<String>>> commentWaiter(@Body OrderCommentParam param);

    /**
     * 获取加单详情
     *
     * @param id
     * @return
     */
    @POST(BASE_URL + "order/overtime/{id}")
    Observable<Response<BaseBean<RenewDetail>>> getRenewDetail(@Path("id") String id);


    /**
     * 获取延时id列表
     *
     * @return
     */
    @POST(BASE_URL + "order/overtime/list/member")
    Observable<Response<BaseBean<List<RenewDetail>>>> getRenewList();

    /**
     * 获取订单提醒列表
     *
     * @param pageNum
     * @param pageSize
     * @param param
     * @return
     */
    @POST(BASE_URL + "push/query")
    Observable<Response<BaseBean<MsgOrderNoticeResult>>> getMsgOrderNoticeList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Body MsgOrderNoticeParam param);

    /**
     * 获取在线陪诊员数量
     *
     * @param param
     * @return
     */
    @POST(BASE_URL + "waiter/onlineCount")
    Observable<Response<BaseBean<String>>> getOnlineWaiterNum(@Body OnlineWaiterParam param);

    /**
     * 会员取消订单原因
     * @return
     */
    @POST(BASE_URL + "common/dictList/member_cancele")
    Observable<Response<BaseBean<OrderCancelReasonList>>> getCancelReasonList();


}
