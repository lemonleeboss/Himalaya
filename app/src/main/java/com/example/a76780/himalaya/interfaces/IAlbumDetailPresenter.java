package com.example.a76780.himalaya.interfaces;

import com.example.a76780.himalaya.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {
    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();
    /**
     * 上拉加载跟多内容
     */
    void loadMore();
    /**
     * 获取专辑详情
     */
    void getAlbumDetail(int albumId, int page);


}
