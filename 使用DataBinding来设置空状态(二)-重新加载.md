### 写在前面

这是[使用DataBinding来设置空状态](http://www.jianshu.com/p/276c8aa80f20)的第二篇，在上一篇中介绍了基本的绑定空状态的操作，而这一篇将在上一篇的基础上添加重新加载的功能，内容不多，但是还是蛮必要的。

看看效果

![screenShot.gif](https://github.com/ditclear/StateBinding/blob/master/screenshot.gif?raw=true)



### 首先

修改我们的空状态布局，添加一个重试的`button`

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
                android:visibility="@{stateModel.progress?View.VISIBLE:View.GONE}"/>

        <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_alignParentBottom="true"
                android:layout_alignParentLeft="true"
                android:layout_alignParentStart="true"
                android:gravity="center"
                android:orientation="vertical"
                android:visibility="@{stateModel.progress?View.INVISIBLE:View.VISIBLE}">

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

        <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:background="@color/colorAccent"
                android:text="重新加载"
                android:textColor="@color/background"/>
    </RelativeLayout>
</layout>
```

如何利用这个`button`呢？对于重新加载这个功能来说，在这个视图里我们也仅仅需要考虑两点罢了

1. 显示隐藏
2. 重新加载事件

对于第一点，参照`ContentLoadingProgressBar`的方式，我们只需要根据error_code判断一下是否显示

而第二点，由于databinding可以在xml里绑定事件，所以我们可以添加一个onclick事件给`button`

```java
	// 显示重新加载
    public boolean isNetError() {
        return this.emptyState == EmptyState.NET_ERROR;
    }
	//重新加载
    public void reload() {
        if (mCallBack != null) {
            mCallBack.onReload();
        }
    }
	
    public void attach(CallBack callBack) {
        mCallBack = callBack;
    }

    public void detach() {
        this.mCallBack = null;
    }
	//使用回调处理错误及重新加载事件
    public interface CallBack {
        //失败
        public void onFailure(Throwable e);

        //重新加载
        public void onReload();
    }
```

xml布局

```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    android:background="@color/colorAccent"
    android:onClick="@{()->stateModel.reload()}"
    android:text="重新加载"
    android:textColor="@color/background"
    android:visibility="@{stateModel.netError?View.VISIBLE:View.GONE}"/>
```

如此，我们的功能就大体完成了，easy

### 实际运用

由于需要处理回调的事件，基本每个页面都需要用到，所以建议在基类里做处理。

一般而言，基类通常会是抽象类。以笔者的习惯来说，基类里一般会有四个抽象方法。

- protected abstract int getLayoutId();**返回布局id**

- protected abstract void initView();**初始化view**

-  protected abstract void initEvent();**处理事件**

- protected abstract void loadData(boolean isRefresh);

  > 加载数据，和重新加载操作一样，所以我们只需要重新调用loadData方法就好了

##### BaseActivity

```java
/**
 * 页面描述：基类 ，协同stateModel处理error及重新加载操作
 *
 * Created by ditclear on 2017/7/1.
 */

public abstract class BaseActivity<VB extends ViewDataBinding> extends AppCompatActivity implements StateModel.CallBack {

    protected StateModel mStateModel;
    protected VB mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mStateModel = new StateModel();
        mStateModel.attach(this);
        
        initView();//初始化view
        initEvent();//处理事件
        loadData(true);//加载数据
    }

    protected abstract void initView();

    protected abstract void initEvent();

    protected abstract void loadData(boolean isRefresh);

    @LayoutRes
    //返回布局id
    protected abstract int getLayoutId();

    @Override
    public void onFailure(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReload() {
        loadData(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStateModel != null) {
            mStateModel.detach();
        }
    }
}

```

### 写在最后

对于空状态的展示经过这两篇文章，相信大家也有了自己的理解，处理空状态及其事件也明白该怎么做了，easy。

毕竟学到了才是属于自己的，授人以鱼不如授人以渔也正是这个道理。

至于使用databinding来处理空状态也是看了google的[*android*-architecture](https://github.com/googlesamples/android-architecture)受到的启发，推荐想了解架构的同学fork下来学习学习。













