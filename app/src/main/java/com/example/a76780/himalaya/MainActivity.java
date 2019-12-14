package com.example.a76780.himalaya;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a76780.himalaya.adapters.IndicatorAdapter;
import com.example.a76780.himalaya.adapters.MainContentAdapter;
import com.example.a76780.himalaya.adapters.RoundRectImageView;
import com.example.a76780.himalaya.admins.login;
import com.example.a76780.himalaya.data.XimalayaDBHelper;
import com.example.a76780.himalaya.interfaces.IPlayCallback;
import com.example.a76780.himalaya.interfaces.IRecommendPresenter;
import com.example.a76780.himalaya.presenters.PlayerPresenter;
import com.example.a76780.himalaya.presenters.RecommendRresenter;
import com.example.a76780.himalaya.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements IPlayCallback {

    private static final String TAG ="MainActivity" ;
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private TextView mHanderTitle;
    private ImageView mPlayControl;
    private TextView mSubTitle;
    private PlayerPresenter mPlayerPresenter;
    private RoundRectImageView mRoundRectImageView;
    private View mPlayControlItem;
    private View mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEVent();
        //
        initPresenter();

    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

    }

    private void initEVent(){
        mIndicatorAdapter.serOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"click index is -->"+index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                   boolean hasPlayList= mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                    //没有设设置过播放列表，我们就播放默认第一个推荐专辑
                    //第一个推荐专辑，每天都会变的
                        playFirstRecommend();
                    }else {
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        }else {
                            mPlayerPresenter.play();
                        }
                    }

                }
            }
        });
        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList= mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                //跳转到播放器界面
                startActivity(new Intent(MainActivity.this,PlayerActivity.class));
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     *播放第一个推荐内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend=RecommendRresenter.getsInstance().getCurrentRecommend();
        if (currentRecommend != null&&currentRecommend.size()>0) {
            Album album= currentRecommend.get(0);
            long albumId=album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        mMagicIndicator = (MagicIndicator) findViewById(R.id. magic_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        mIndicatorAdapter=new IndicatorAdapter(this);
        CommonNavigator commonNavigator=new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);

        //ViewPager
        mContentPager= (ViewPager) this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter=new MainContentAdapter(supportFragmentManager);


        mContentPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定在一起
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);


        //播放控制相关的
        mRoundRectImageView = this.findViewById(R.id.main_track_cover);
        mHanderTitle = this.findViewById(R.id.main_head_title);
        mHanderTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);
        mPlayControl = this.findViewById(R.id.main_play_control);
        mPlayControlItem = this.findViewById(R.id.main_play_control_item);

        //搜索
        mSearchBtn = this.findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter!=null){
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }
    private void updatePlayControl(boolean isPlaying){
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying?R.drawable.selector_player_pause:R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            LogUtil.d(TAG,"trackTitle-->"+trackTitle);
            if (mHanderTitle != null) {
                mHanderTitle.setText(trackTitle);
            }

            LogUtil.d(TAG,"nickname-->"+nickname);
            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }
            LogUtil.d(TAG,"coverUrlMiddle-->"+coverUrlMiddle);
            Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
