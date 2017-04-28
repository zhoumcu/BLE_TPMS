package com.example.sid_fu.blecentral.db;

import android.content.Context;

import com.example.sid_fu.blecentral.db.entity.RecordData;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：Administrator on 2016/10/12 11:14
 * company: xxxx
 * email：1032324589@qq.com
 */
public class DbObervable {
    private DbHelper helper;

    public static DbObervable getInstance(Context context) {
        return new DbObervable(context);
    }

    private DbObervable(Context context) {
        helper = DbHelper.getInstance(context);
    }

    public Observable<Boolean> updateRecord(int deviceId, String name, RecordData data) {
        Observable<Boolean> observable = Observable.just(helper.update(deviceId,name,data));
        toSubscribe(observable, new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }
        });
        return observable;
    }


    private <T> void toSubscribe(Observable<T> o,Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
