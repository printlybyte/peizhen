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
import com.yinfeng.wypzh.http.common.RetrofitManager;

import io.reactivex.Observable;
import retrofit2.Response;

public class PatientApi {
    private volatile static PatientApi instance;

    private PatientApi() {
    }

    public static PatientApi getInstance() {
        if (instance == null) {
            synchronized (PatientApi.class) {
                if (instance == null)
                    instance = new PatientApi();
            }
        }
        return instance;
    }


    /**
     * 获取就诊人列表数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Observable<Response<BaseBean<PatientListData>>> getPatientList(int pageNum, int pageSize) {
//        PatientListParam bean = new PatientListParam(pageNum, pageSize);
        return RetrofitManager.getInstance().create(PatientService.class).getPatientList(pageNum, pageSize);
    }

    /**
     * 删除就诊人
     *
     * @param ids
     * @return
     */
    public Observable<Response<BaseBean<String>>> deletePatient(String ids) {
//        PatientDeleteParam bean = new PatientDeleteParam(ids);
        return RetrofitManager.getInstance().create(PatientService.class).deletePatient(ids);
    }

    /**
     * 获取病理列表数据
     *
     * @return
     */
    public Observable<Response<BaseBean<SickListData>>> getSickTips() {
        return RetrofitManager.getInstance().create(PatientService.class).getSickTips();
    }

    /**
     * 增加就诊人
     *
     * @param addParam
     * @return
     */
    public Observable<Response<BaseBean<String>>> addPatient(PatientAddParam addParam) {
        return RetrofitManager.getInstance().create(PatientService.class).addPatient(addParam);
    }

    /**
     * 编辑就诊人
     *
     * @param editParam
     * @return
     */
    public Observable<Response<BaseBean<String>>> editPatient(PatientEditParam editParam) {
        return RetrofitManager.getInstance().create(PatientService.class).editPatient(editParam);
    }

    /**
     * 获取医院列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Observable<Response<BaseBean<HospitalListData>>> getHospitalList(int pageNum, int pageSize) {
        HospitalBodyParam name = new HospitalBodyParam("");
        return RetrofitManager.getInstance().create(PatientService.class).getHospitalList(pageNum, pageSize, name);
    }

    /**
     * 搜索医院
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    public Observable<Response<BaseBean<HospitalListData>>> searchHospitalByName(int pageNum, int pageSize, String name) {
        HospitalBodyParam nameBodyParam = new HospitalBodyParam(name);
        return RetrofitManager.getInstance().create(PatientService.class).searchHospitalByName(pageNum, pageSize, nameBodyParam);
    }

    /**
     * 获取陪诊员的评论
     *
     * @param pageNum
     * @param pageSize
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<CommentListResult>>> getCommentList(int pageNum, int pageSize, CommentParam param) {
        return RetrofitManager.getInstance().create(PatientService.class).getCommentList(pageNum, pageSize, param);
    }

    /**
     * 获取陪诊员详情
     *
     * @param waiterId
     * @return
     */
    public Observable<Response<BaseBean<WaiterInfo>>> getWaiterInfo(String waiterId) {
        return RetrofitManager.getInstance().create(PatientService.class).getWaiterInfo(waiterId);
    }
}
