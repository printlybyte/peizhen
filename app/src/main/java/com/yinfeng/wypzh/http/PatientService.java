package com.yinfeng.wypzh.http;

import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.patient.HospitalBodyParam;
import com.yinfeng.wypzh.bean.patient.HospitalListData;
import com.yinfeng.wypzh.bean.patient.PatientAddParam;
import com.yinfeng.wypzh.bean.patient.PatientEditParam;
import com.yinfeng.wypzh.bean.patient.PatientListData;
import com.yinfeng.wypzh.bean.patient.SickListData;
import com.yinfeng.wypzh.bean.waiter.CommentListResult;
import com.yinfeng.wypzh.bean.waiter.CommentParam;
import com.yinfeng.wypzh.bean.waiter.WaiterInfo;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.yinfeng.wypzh.http.common.ApiContents.BASE_URL;

public interface PatientService {
//    /**
//     * 获取就诊人列表数据
//     *
//     * @param bean
//     * @return
//     */
//    @POST("http://192.168.16.59:8013/admin/memberPatient/query")
//    Observable<PatientListResult> getPatientList(@Body PatientListParam bean);


    /**
     * 获取就诊人列表数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @POST(BASE_URL + "memberPatient/query")
    @FormUrlEncoded
    Observable<Response<BaseBean<PatientListData>>> getPatientList(@Field("pageNum") int pageNum, @Field("pageSize") int pageSize);


    /**
     * 删除 就诊人
     *
     * @return
     */

    @POST(BASE_URL + "memberPatient/delete")
    @FormUrlEncoded
    Observable<Response<BaseBean<String>>> deletePatient(@Field("ids") String ids);

    /**
     * 获取病理列表数据
     *
     * @return
     */
    @POST(BASE_URL + "memberPatientEdical/queryAll")
    Observable<Response<BaseBean<SickListData>>> getSickTips();

    /**
     * 增加就诊人
     *
     * @param addParam
     * @return
     */
    @POST(BASE_URL + "memberPatient/save")
    Observable<Response<BaseBean<String>>> addPatient(@Body PatientAddParam addParam);

    /**
     * 修改就诊人
     *
     * @param editParam
     * @return
     */
    @POST(BASE_URL + "memberPatient/save")
    Observable<Response<BaseBean<String>>> editPatient(@Body PatientEditParam editParam);


    /**
     * 获取医院列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @POST(BASE_URL + "hospital/queryPage")
    Observable<Response<BaseBean<HospitalListData>>> getHospitalList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Body HospitalBodyParam name);

    /**
     * 搜索医院
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @POST(BASE_URL + "hospital/queryPage")
    Observable<Response<BaseBean<HospitalListData>>> searchHospitalByName(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Body HospitalBodyParam name);

    /**
     * 获取陪诊员的评论
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @POST(BASE_URL + "waiterEva/list")
    Observable<Response<BaseBean<CommentListResult>>> getCommentList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Body CommentParam name);

    @POST(BASE_URL + "waiter/get")
    Observable<Response<BaseBean<WaiterInfo>>> getWaiterInfo(@Query("id") String id);

}
