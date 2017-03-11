package com.ditclear.app.state;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ditclear.app.state.EmptyState.EMPTY;
import static com.ditclear.app.state.EmptyState.NET_ERROR;
import static com.ditclear.app.state.EmptyState.NORMAL;
import static com.ditclear.app.state.EmptyState.NOT_AVAILABLE;
import static com.ditclear.app.state.EmptyState.PROGRESS;

/**
 * 页面描述：空状态
 * <p>
 * Created by ditclear on 2017/2/24.
 */
@IntDef({NORMAL, PROGRESS, EMPTY, NET_ERROR, NOT_AVAILABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface EmptyState {

    int NORMAL = -1;  //正常
    int PROGRESS = -2;//显示进度条

    int EMPTY = 11111; //列表数据为空
    int NET_ERROR = 22222;  //网络未连接
    int NOT_AVAILABLE = 33333; //服务器不可用

    //...各种页面的空状态，可以自己定义、添加

}
