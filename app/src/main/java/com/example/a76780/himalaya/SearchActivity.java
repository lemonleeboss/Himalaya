package com.example.a76780.himalaya;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a76780.himalaya.base.BaseActivity;
import com.example.a76780.himalaya.interfaces.ISearchCallback;
import com.example.a76780.himalaya.presenters.SearchPresenter;
import com.example.a76780.himalaya.utils.LogUtil;
import com.example.a76780.himalaya.views.FlowTextLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

    private static final String TAG = "SearchActivity";
    private View mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        //注册UI更新的接口
        mSearchPresenter.registerViewCallback(this);
        //去拿热词
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            //干掉UI更新的接口
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter=null;
        }
    }

    private void initEvent() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去执行搜索逻辑
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                LogUtil.d(TAG,"content-->"+s);
//                LogUtil.d(TAG,"start-->"+start);
//                LogUtil.d(TAG,"before-->"+before);
//                LogUtil.d(TAG,"count-->"+count);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(){
        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mSearchBtn = this.findViewById(R.id.search_btn);
          mResultContainer = this.findViewById(R.id.search_container);
        mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {

        LogUtil.d(TAG,"hotWordList-->"+hotWordList.size());

        List<String> hotWords=new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord=hotWord.getSearchword();
            hotWords.add(searchWord);
        }
        //更新UI
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }
}
