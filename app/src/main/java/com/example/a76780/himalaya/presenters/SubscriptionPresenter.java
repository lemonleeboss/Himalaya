package com.example.a76780.himalaya.presenters;

import com.example.a76780.himalaya.base.BaseApplication;
import com.example.a76780.himalaya.data.SubscriptionDao;
import com.example.a76780.himalaya.interfaces.ISubDaoCallback;
import com.example.a76780.himalaya.interfaces.ISubscriptionCallBack;
import com.example.a76780.himalaya.interfaces.ISubscriptionpresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionpresenter, ISubDaoCallback {

    private final SubscriptionDao mSubscriptionDao;
    private Map<Long,Album> mData=new HashMap<>();
    private List<ISubscriptionCallBack> mCallBacks=new ArrayList<>();

    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
        listSubscriptions();

    }

    private void listSubscriptions(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
    private static SubscriptionPresenter sInstance=null;

    public static SubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class){
                sInstance=new SubscriptionPresenter();
            }
        }
        return sInstance;
    }

    @Override
    public void addSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscription(Album album) {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result=mData.get(album.getId());
        //不为空，表示已经订阅
        return result!=null;

    }

    @Override
    public void registerViewCallback(ISubscriptionCallBack iSubscriptionCallBack) {
        if (mCallBacks.contains(iSubscriptionCallBack)) {
            mCallBacks.add(iSubscriptionCallBack);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallBack iSubscriptionCallBack) {
        mCallBacks.remove(iSubscriptionCallBack);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        //添加结果的回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack callBack : mCallBacks) {
                    callBack.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        //删除订阅的回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack callBack : mCallBacks) {
                    callBack.onDeleteResult(isSuccess);
                }
            }
        });

    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        //加载数据的回调
        for (Album album : result) {
            mData.put(album.getId(),album);
        }
        //通知UI更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack callBack : mCallBacks) {
                    callBack.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
