package com.ditclear.app.state;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.ditclear.app.MyApp;
import com.ditclear.app.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 页面描述：状态页面设置模型
 * <p>
 * Created by ditclear on 2017/2/24.
 */

public class StateModel extends BaseObservable {

    private Context mContext = MyApp.instance();

    @EmptyState
    private int emptyState = EmptyState.NORMAL;

    private CallBack mCallBack;

    public int getEmptyState() {
        return emptyState;
    }

    /**
     * 设置状态
     */
    public void setEmptyState(@EmptyState int emptyState) {
        this.emptyState = emptyState;
        notifyChange();
    }


    /**
     * 根据异常显示状态
     */
    public void bindThrowable(Throwable e) {
        bindThrowable(e, true);
    }

    /**
     * 提示异常错误
     *
     * @param e            异常信息
     * @param showNetError 是否显示除空数据异常的其它信息
     */
    public void bindThrowable(Throwable e, boolean showNetError) {

        if (e instanceof EmptyException) {
            if (((EmptyException) e).getCode() == EmptyState.NET_ERROR) {
                bindThrowable(new ConnectException());
            } else {
                setEmptyState(((EmptyException) e).getCode());
            }
        } else if (e instanceof SocketTimeoutException) {
            if (mCallBack != null) {
                mCallBack.onFailure(new Throwable("网络连接超时"));
            }
        } else if (e instanceof UnknownHostException || e instanceof ConnectException) {
            //网络未连接
            if (mCallBack != null) {
                mCallBack.onFailure(new Throwable("网络未连接"));
            }
            if (showNetError) {
                setEmptyState(EmptyState.NET_ERROR);
            }
        } else {
            if (mCallBack != null) {
                mCallBack.onFailure(e);
            }
        }

    }

    //重新加载
    public void reload() {
        if (mCallBack != null) {
            mCallBack.onReload();
        }
    }

    /**
     * 显示进度条
     */
    public boolean isProgress() {
        return this.emptyState == EmptyState.PROGRESS;
    }

    /**
     * 显示重新加载
     */
    public boolean isNetError() {
        return this.emptyState == EmptyState.NET_ERROR;
    }

    /**
     * 显示空视图
     */
    public boolean isEmpty() {
        return this.emptyState != EmptyState.NORMAL;
    }

    /**
     * 空状态信息
     */
    @Bindable
    public String getCurrentStateLabel() {

        switch (emptyState) {
            case EmptyState.EMPTY:
                return mContext.getString(R.string.no_data);
            case EmptyState.NOT_AVAILABLE:
                return mContext.getString(R.string.server_not_avaliabe);
            default:
                return "";
        }
    }

    /**
     * 空状态图片
     */
    @Bindable
    public Drawable getEmptyIconRes() {
        switch (emptyState) {
            case EmptyState.EMPTY:
                return ContextCompat.getDrawable(mContext,
                        R.drawable.ic_visibility_off_green_400_48dp);
            case EmptyState.NOT_AVAILABLE:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_cloud_off_green_400_48dp);
            default:
                return null;
        }
    }

    public void attach(CallBack callBack) {
        mCallBack = callBack;
    }

    public void detach() {
        this.mCallBack = null;
    }

    public interface CallBack {
        //失败
        public void onFailure(Throwable e);

        //重新加载
        public void onReload();
    }

}
