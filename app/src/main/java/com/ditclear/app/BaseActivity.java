package com.ditclear.app;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ditclear.app.state.StateModel;

/**
 * 页面描述：基类 ，协同stateModel处理error及重新加载操作
 *
 * Created by ditclear on 2017/7/1.
 */

public abstract class BaseActivity<VB extends ViewDataBinding> extends AppCompatActivity implements
        StateModel.CallBack {

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
