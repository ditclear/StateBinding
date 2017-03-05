package com.ditclear.app;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 页面描述：异步操作
 * <p>
 * Created by ditclear on 2017/2/22.
 */

public class RxUtil {

    private RxUtil() {
    }

    public static <T> Observable.Transformer<T, T> normalSchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> source) {
                return source.subscribeOn(Schedulers.io()).delay(150, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable.Transformer<T, T> normalSchedulers(final StateModel stateModel) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> source) {
                return source.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                stateModel.setEmptyState(EmptyState.NORMAL);
                            }
                        }).doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                stateModel.bindThrowable(throwable);
                            }
                        });
            }
        };
    }
}
