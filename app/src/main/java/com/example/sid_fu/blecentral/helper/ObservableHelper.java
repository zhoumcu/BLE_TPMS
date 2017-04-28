package com.example.sid_fu.blecentral.helper;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.db.entity.Device;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：Administrator on 2016/10/24 10:26
 * company: xxxx
 * email：1032324589@qq.com
 */
public class ObservableHelper {
    private static ObservableHelper ourInstance = new ObservableHelper();

    public static ObservableHelper getInstance() {
        return ourInstance;
    }

    private ObservableHelper() {
    }
    public Observable<Device> getDevice(int deviceId){
        Observable<Device> observable = Observable.just(App.getDeviceDao().get(deviceId));
        toSubscribe(observable, new Subscriber<Device>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Device device) {

            }
        });
        return observable;
    }
    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
