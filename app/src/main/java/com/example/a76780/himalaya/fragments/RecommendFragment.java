package com.example.a76780.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.a76780.himalaya.DetailActivity;
import com.example.a76780.himalaya.R;
import com.example.a76780.himalaya.adapters.RecommendListAdapter;
import com.example.a76780.himalaya.base.BaseFragment;
import com.example.a76780.himalaya.interfaces.IRecommendViewCallback;
import com.example.a76780.himalaya.presenters.AlbumDetailPresenter;
import com.example.a76780.himalaya.presenters.RecommendRresenter;
import com.example.a76780.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.onRecommendItemClickListener {
    private static final String TAG ="RecommendFragment" ;
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendRresenter mRecommendRresenter;
    private UILoader mUiLoader;
    TextView tv;
    Button btn_exit;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

        mUiLoader=new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //获取逻辑层的对象
        mRecommendRresenter = RecommendRresenter.getsInstance();
        //先要设置通知接口的注册
        mRecommendRresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendRresenter.getRecommendList();
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }
        mUiLoader.setOnRetryClickListener(this);
        //返回view,给界面显示
        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        //RecycleView的使用
        //1.找到控件
        mRecommendRv=mRootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout=mRootView.findViewById(R.id.cover_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top=UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);

            }
        });
        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListner(this);
        return mRootView;
    }


    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器，并且更新UI
        mRecommendListAdapter.setData(albumList);
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到内容的时候，这个方法就会被调用(成功了)
        //数据回来以后，就是跟新Ui了
        //把数据设置给适配器，并且更新UI
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);

    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (mRecommendRresenter != null) {
            mRecommendRresenter.unRegisterViewCallback(this);
        }
    }


    @Override
    public void onRetryClick() {
        if (mRecommendRresenter != null) {
            mRecommendRresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了,跳转到详情界面
        Intent intent=new Intent(getContext(),DetailActivity.class);
        startActivity(intent);
    }
}
