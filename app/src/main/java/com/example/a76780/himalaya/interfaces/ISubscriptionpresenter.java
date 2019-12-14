package com.example.a76780.himalaya.interfaces;

import com.example.a76780.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅一般有上限，比如说不超过一百个
 */
public interface ISubscriptionpresenter extends IBasePresenter<ISubscriptionCallBack> {

    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     * @param album
     */
    void getSubscription(Album album);

    /**
     * 判断当前专辑是否已经订阅
     * @param album
     */
    boolean isSub(Album album);
}
