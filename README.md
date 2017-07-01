# StateBinding

#### 使用DataBinding来为android设置状态信息  

简书：[http://www.jianshu.com/p/276c8aa80f20](http://www.jianshu.com/p/276c8aa80f20)

![enframe_2017-03-11-17-03-15.png](http://upload-images.jianshu.io/upload_images/3722695-c6e983b74e962283.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/250)![enframe_2017-03-11-17-03-49.png](http://upload-images.jianshu.io/upload_images/3722695-647a1a2a7ead52f0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/250)![enframe_2017-03-11-17-03-57.png](http://upload-images.jianshu.io/upload_images/3722695-eb885266b5121a2b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/250)![enframe_2017-03-11-17-04-05.png](http://upload-images.jianshu.io/upload_images/3722695-453a58044ec60ed9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/250)
![enframe_2017-03-11-17-26-51.png](http://upload-images.jianshu.io/upload_images/3722695-47f42990dcdf0c83.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/250)



#### ***写在前面***

在平时的开发之中，我们需要对于数据加载的情况进行展示：

- 空数据
- 网络异常
- 加载中等等情况

现在设置页面状态的方式有多种，由于笔者近期一直在使用databinding，而数据绑定通过改变模型来展示view的方式和状态页的设置也满契合的。

所以这里就讲讲使用databinding来设置android中的各种状态页。很简单，先看看效果

![screnshot.gif](https://github.com/ditclear/StateBinding/blob/master/screnshot.gif)



没了解过databinding的可以先了解一下

> [Data Binding（数据绑定）用户指南](http://www.jcodecraeer.com/a/anzhuokaifa/developer/2015/0606/3005.html)

#### 首先

在app的build.gradle文件中开启databinding

```java
android{
	...
	dataBinding {
        enabled = true
    }
}
```

我们先定义一些用于状态的注解`EmptyState`

```java
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
```

再自定义一个异常`EmptyException`用于显示我们需要的状态信息

```java
/**
 * 页面描述：异常
 * <p>
 * Created by ditclear on 2017/3/5.
 */
public class EmptyException extends Exception {

    private int code;

    public EmptyException(@EmptyState int code) {
        super();
        this.code = code;
    }


    @EmptyState
    public int getCode() {
        return code;
    }

    public void setCode(@EmptyState int code) {
        this.code = code;
    }
}

```

现在，大多数展示状态页的控件都会提供

1. 加载中的进度条
2. 错误信息
3. 空状态
4. ...

所以我们的目标也是显示这些

#### 布局

以数据绑定的形式进行布局，使用`StateModel`来控制状态页展示的消息

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    >

    <data>

        <import type="android.view.View"/>

        <variable
            name="stateModel"
            type="com.ditclear.app.state.StateModel"/>
    </data>

    <RelativeLayout
        android:id="@+id/rv_empty_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/background"
        android:clickable="true"
        android:focusableInTouchMode="true"
        android:visibility="@{stateModel.empty?View.VISIBLE:View.GONE}">

        <android.support.v4.widget.ContentLoadingProgressBar
            style="?android:attr/progressBarStyle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:visibility="@{stateModel.showProgress()?View.VISIBLE:View.GONE}"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_alignParentBottom="true"
            android:layout_alignParentLeft="true"
            android:layout_alignParentStart="true"
            android:gravity="center"
            android:orientation="vertical"
            android:visibility="@{stateModel.showProgress()?View.INVISIBLE:View.VISIBLE}">

            <ImageView
                android:id="@+id/none_data"
                android:layout_width="345dp"
                android:layout_height="180dp"
                android:scaleType="fitCenter"
                android:src="@{stateModel.emptyIconRes}"/>


            <TextView
                android:layout_width="wrap_content"
                android:layout_height="25dp"
                android:layout_below="@+id/none_data"
                android:layout_centerHorizontal="true"
                android:text="@{stateModel.currentStateLabel}"
                android:textSize="16sp"/>


        </LinearLayout>
    </RelativeLayout>
</layout>
```

布局文件中有几个方法

- empty 用于控制状态页是显示还是隐藏，数据加载正常（即状态为NORMAL）的时候隐藏，否则展示
- isProgress 是否显示加载中，如果显示进度条（即状态为PROGRESS），就隐藏异常页
- emptyIconRes  显示状态的图片信息
- currentStateLabel  显示状态的文字消息

我们定义状态的ViewModel ，就叫`StateModel`，来控制状态

```java
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
```

很普通的视图模型，主要有几个用于判断状态显示的方法

- bindThrowable 根据异常显示状态
- **setEmptyState** 方法用来设置当前的状态，通过`notifyChange`来通知布局文件改变

##### 下面讲讲实际运用：

在`activity`或者`fragment`布局中，添加状态页的布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>

        <import type="android.view.View"/>

        <variable
            name="stateModel"
            type="com.ditclear.app.state.StateModel"/>

    </data>


        <FrameLayout
            android:id="@+id/container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">


            <android.support.v4.widget.NestedScrollView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:fillViewport="false"
                android:overScrollMode="always"
                android:visibility="@{stateModel.empty?View.GONE:View.VISIBLE}">

                <TextView
                    android:id="@+id/content_tv"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:gravity="center"/>


            </android.support.v4.widget.NestedScrollView>


            <include
                layout="@layout/widget_layout_empty"
                app:stateModel="@{stateModel}"/>

        </FrameLayout>
</layout>
```

最后在`activity`或者`fragment`中我们只需要通过`state.bindThrowable()`和`state.setEmptyState()`方法便可以轻松设置各种各样的状态。

```java
observable.subscribe(new Subscriber<List<Contributor>>() {

            @Override
            public void onStart() {
                super.onStart();
                mStateModel.setEmptyState(EmptyState.PROGRESS);

            }

            @Override
            public void onCompleted() {
                mStateModel.setEmptyState(EmptyState.NORMAL);

            }

            @Override
            public void onError(Throwable e) {
                mStateModel.setEmptyState(EmptyState.NORMAL);
                mStateModel.bindThrowable(e);
            }

            @Override
            public void onNext(List<Contributor> contributors) {
                mStateModel.setEmptyState(EmptyState.NORMAL);
                if (contributors == null || contributors.isEmpty()) {
                    onError(new EmptyException(EmptyState.EMPTY));
                } else {
                    mBinding.contentTv.setText(contributors.toString());
                }
            }
        });
```

#### 写在最后

对于要使用数据来控制视图状态的，使用databinding实在是一个事半功倍的方式。而且也十分容易理解。

