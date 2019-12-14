package com.example.a76780.himalaya.interfaces;

import com.example.a76780.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback  {

    /**
     * 搜索结果的回调方法
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的结果回调方法
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     * @param result 结果
     * @param isOkay ture 表示加载更多成功，false表示加载没有更多
     */
    void onLoadMoreResult(List<Album> result,boolean isOkay);

    /**
     * 联想关键字的结果回调方法
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

}
