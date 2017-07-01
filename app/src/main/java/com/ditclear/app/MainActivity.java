package com.ditclear.app;

import android.view.Menu;
import android.view.MenuItem;

import com.ditclear.app.databinding.ActivityMainBinding;
import com.ditclear.app.state.EmptyException;
import com.ditclear.app.state.EmptyState;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends BaseActivity<ActivityMainBinding> {


    private Observable<List<Contributor>> mListObservable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        mListObservable = loadData();

        mBinding.setStateModel(mStateModel);

    }

    @Override
    protected void initEvent() {


    }

    @Override
    protected void loadData(boolean isRefresh) {
        sub(loadData());
    }


    /**
     * 加载网络数据
     */
    private Observable<List<Contributor>> loadData() {
        return GitHubFactory.getInstance()
                .create(GitHub.class)
                .contributors("square", "retrofit")
                .delay(2, TimeUnit.SECONDS)
                .compose(RxUtil.<List<Contributor>>normalSchedulers());

    }

    private void sub(Observable<List<Contributor>> observable) {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_progress:
                mListObservable = loadData();
                break;
            case R.id.menu_empty:
                mListObservable = loadData().compose(
                        new Observable.Transformer<List<Contributor>,List<Contributor>>() {
                            @Override
                            public Observable<List<Contributor>> call(
                                    Observable<List<Contributor>> listObservable) {
                                return Observable.error(new EmptyException(EmptyState.EMPTY));
                            }
                        });
                break;
            case R.id.menu_no_net:
                mListObservable = loadData().compose(
                        new Observable.Transformer<List<Contributor>,List<Contributor>>() {
                            @Override
                            public Observable<List<Contributor>> call(
                                    Observable<List<Contributor>> listObservable) {
                                return Observable.error(new EmptyException(EmptyState.NET_ERROR));
                            }

                        });

                break;
            case R.id.menu_no_server:
                mListObservable = loadData().compose(
                        new Observable.Transformer<List<Contributor>,List<Contributor>>() {
                            @Override
                            public Observable<List<Contributor>> call(
                                    Observable<List<Contributor>> listObservable) {
                                return Observable.error(new EmptyException(
                                        EmptyState.NOT_AVAILABLE));
                            }
                        });
                break;

        }
        sub(mListObservable);
        return super.onOptionsItemSelected(item);
    }
}
