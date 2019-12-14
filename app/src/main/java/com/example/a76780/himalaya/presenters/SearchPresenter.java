package com.example.a76780.himalaya.presenters;

import android.support.annotation.Nullable;

import com.example.a76780.himalaya.data.XimalayaApi;
import com.example.a76780.himalaya.interfaces.ISearchCallback;
import com.example.a76780.himalaya.interfaces.ISearchPresenter;
import com.example.a76780.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //当前的搜索关键字
    private String mCurrentKeyword=null;
    private  XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE=1;
    private int mCurrentPage=DEFAULT_PAGE;


    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private static SearchPresenter sSearchPresenter=null;

    public static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class){
                if (sSearchPresenter == null) {
                    sSearchPresenter=new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallback> mCallback = new ArrayList<>();
    @Override
    public void doSearch(String keyword) {

        //用于重新搜索
        //当网络不好的时候，用户会点击重新搜素
        this.mCurrentKeyword=keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {

                }else {

                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords=hotWordList.getHotWordList();
                    LogUtil.d(TAG,"hotWordList-->"+hotWords.size());
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (mCallback.contains(iSearchCallback)) {
            mCallback.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallback.remove(iSearchCallback);

    }
}
