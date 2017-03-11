package com.ditclear.app.state;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.ditclear.app.MyApp;
import com.ditclear.app.R;

/**
 * 页面描述：状态页面设置模型
 * <p>
 * Created by ditclear on 2017/2/24.
 */

public class StateModel extends BaseObservable {

    private Context mContext = MyApp.instance();

    @EmptyState
    private int emptyState = EmptyState.NORMAL;

    private boolean empty;

    public int getEmptyState() {
        return emptyState;
    }

    /**
     * 设置状态
     *
     * @param emptyState
     */
    public void setEmptyState(@EmptyState int emptyState) {
        this.emptyState = emptyState;
        notifyChange();
    }

    /**
     * 显示进度条
     *
     * @return
     */
    public boolean isProgress() {
        return this.emptyState == EmptyState.PROGRESS;
    }

    /**
     * 根据异常显示状态
     *
     * @param e
     */
    public void bindThrowable(Throwable e) {
        if (e instanceof EmptyException) {
            @EmptyState
            int code = ((EmptyException) e).getCode();

            setEmptyState(code);
        }
    }

    public boolean isEmpty() {
        return this.emptyState != EmptyState.NORMAL;
    }

    /**
     * 空状态信息
     *
     * @return
     */
    @Bindable
    public String getCurrentStateLabel() {

        switch (emptyState) {
            case EmptyState.EMPTY:
                return mContext.getString(R.string.no_data);
            case EmptyState.NET_ERROR:
                return mContext.getString(R.string.please_check_net_state);
            case EmptyState.NOT_AVAILABLE:
                return mContext.getString(R.string.server_not_avaliabe);
            default:
                return mContext.getString(R.string.no_data);
        }
    }

    /**
     * 空状态图片
     *
     * @return
     */
    @Bindable
    public Drawable getEmptyIconRes() {
        switch (emptyState) {
            case EmptyState.EMPTY:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_visibility_off_green_400_48dp);
            case EmptyState.NET_ERROR:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_off_green_400_48dp);
            case EmptyState.NOT_AVAILABLE:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_cloud_off_green_400_48dp);
            default:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_visibility_off_green_400_48dp);
        }
    }

}
