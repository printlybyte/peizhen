package com.yinfeng.wypzh.utils;

import com.yinfeng.wypzh.bean.realmbean.SickBean;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;

/**
 * @author Asen
 */
public class RealmManager {

    private static volatile RealmManager instance;

    public static RealmManager getInstance() {
        if (instance == null) {
            synchronized (RealmManager.class) {
                if (instance == null) {
                    instance = new RealmManager();
                }
            }
        }
        return instance;
    }


    public void saveSickListOrUpdate(Realm realm, final List<SickBean> list) {
        if (list == null || list.size() == 0)
            return;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < list.size(); i++) {
                    SickBean bean = list.get(i);
                    realm.copyToRealmOrUpdate(bean);
                }
            }
        });
    }

    public void saveSickBeanOrUpdate(Realm realm, final SickBean bean) {
        if (bean == null)
            return;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(bean);
            }
        });
    }

//    public void getSicks(Realm realm) {
//        RealmAsyncTask transaction = realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                //成功回调
//            }
//        }, new Realm.Transaction.OnError() {
//            @Override
//            public void onError(Throwable error) {
//                //失败回调
//            }
//        });
//    }
}
