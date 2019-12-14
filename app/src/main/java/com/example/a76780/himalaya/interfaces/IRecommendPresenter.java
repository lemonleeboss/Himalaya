package com.example.a76780.himalaya.interfaces;

import com.example.a76780.himalaya.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();
    /**
     * 上拉加载跟多内容
     */
    void loadMore();

}
