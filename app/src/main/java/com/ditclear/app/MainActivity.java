package com.ditclear.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ditclear.app.databinding.ActivityMainBinding;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mMainBinding;

    private StateModel mStateModel;

    private Observable<List<Contributor>> mListObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mStateModel = new StateModel();

        mListObservable = loadData();

        mMainBinding.setStateModel(mStateModel);

        mMainBinding.refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));

        mMainBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sub(loadData());
            }
        });
    }

    private Observable<List<Contributor>> loadData() {
        return GitHubFactory.getInstance()
                .create(GitHub.class)
                .contributors("square", "retrofit")
                .compose(RxUtil.<List<Contributor>>normalSchedulers(mStateModel));

    }

    private void sub(Observable<List<Contributor>> observable) {
        observable.subscribe(new Subscriber<List<Contributor>>() {
            @Override
            public void onCompleted() {
                mMainBinding.refreshLayout.setRefreshing(false);
                mStateModel.setEmptyState(EmptyState.NORMAL);

            }

            @Override
            public void onError(Throwable e) {
                mMainBinding.refreshLayout.setRefreshing(false);
                mStateModel.bindThrowable(e);
                Toast.makeText(MainActivity.this, mStateModel.getCurrentStateLabel(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNext(List<Contributor> contributors) {
                if (contributors == null || contributors.isEmpty()) {
                    onError(new EmptyException(EmptyState.EMPTY));
                } else {
                    mMainBinding.contentTv.setText(contributors.toString());
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
        if (!mMainBinding.refreshLayout.isRefreshing()) {
            mMainBinding.refreshLayout.setRefreshing(true);
        }
        switch (item.getItemId()) {
            case R.id.menu_normal:
                mListObservable = loadData();
                break;
            case R.id.menu_empty:
                mListObservable = loadData().compose(new Observable.Transformer<List<Contributor>, List<Contributor>>() {
                    @Override
                    public Observable<List<Contributor>> call(Observable<List<Contributor>> listObservable) {
                        return Observable.create(new Observable.OnSubscribe<List<Contributor>>() {
                            @Override
                            public void call(Subscriber<? super List<Contributor>> subscriber) {
                                subscriber.onError(new EmptyException(EmptyState.EMPTY));
                            }
                        });
                    }
                });
                break;
            case R.id.menu_no_net:
                mListObservable = loadData().compose(new Observable.Transformer<List<Contributor>, List<Contributor>>() {
                    @Override
                    public Observable<List<Contributor>> call(Observable<List<Contributor>> listObservable) {
                        return Observable.create(new Observable.OnSubscribe<List<Contributor>>() {
                            @Override
                            public void call(Subscriber<? super List<Contributor>> subscriber) {
                                subscriber.onError(new EmptyException(EmptyState.NET_ERROR));
                            }
                        });
                    }
                });
                break;
            case R.id.menu_no_server:
                mListObservable = loadData().compose(new Observable.Transformer<List<Contributor>, List<Contributor>>() {
                    @Override
                    public Observable<List<Contributor>> call(Observable<List<Contributor>> listObservable) {
                        return Observable.create(new Observable.OnSubscribe<List<Contributor>>() {
                            @Override
                            public void call(Subscriber<? super List<Contributor>> subscriber) {
                                subscriber.onError(new EmptyException(EmptyState.NOT_AVAILABLE));
                            }
                        });
                    }
                });
                break;

        }
        sub(mListObservable);
        return super.onOptionsItemSelected(item);
    }
}
