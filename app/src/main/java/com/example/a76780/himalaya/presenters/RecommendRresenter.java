package com.example.a76780.himalaya.presenters;

import android.support.annotation.Nullable;

import com.example.a76780.himalaya.data.XimalayaApi;
import com.example.a76780.himalaya.interfaces.IRecommendPresenter;
import com.example.a76780.himalaya.interfaces.IRecommendViewCallback;
import com.example.a76780.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendRresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendRresenter";
    private List<IRecommendViewCallback> mCallbacks=new ArrayList<>();
    private List<Album> mCurrentRecommend=null;

    private RecommendRresenter(){

    }
    private static RecommendRresenter sInstance=null;

    /**
     * 获取单例对象
     * @return
     */
    public static RecommendRresenter getsInstance(){
        if (sInstance == null) {
            synchronized (RecommendRresenter.class){
                if (sInstance == null) {
                    sInstance=new RecommendRresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前的推荐专辑列表
     *
     * @return推荐专辑列表，使用前要判空
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }
    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 这个接口：3.10.6获取你喜欢的专辑
     */
    @Override
    public void getRecommendList() {
        //封装参数
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                //获取数据成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //数据回来以后,我们要去更新UI
                    // upRecommandUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取数据失败
                LogUtil.d(TAG,"error-->"+i);
                LogUtil.d(TAG,"errorMsg-->"+s);
                handlerError();
            }
        });

    }

    private void handlerError() {
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
            //测试，清空一下，让界面显示为空
            //albumList.clear();
            if (albumList.size()==0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            }else {
                    for (IRecommendViewCallback callback : mCallbacks) {

                        callback.onRecommendListLoaded(albumList);
                    }
                    this.mCurrentRecommend=albumList;
                }

            }
        }
        private void updateLoading(){
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onLoading();
            }
        }



    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null&&!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(mCallbacks);
        }
    }
}
