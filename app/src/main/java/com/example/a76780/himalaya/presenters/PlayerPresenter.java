package com.example.a76780.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.a76780.himalaya.data.XimalayaApi;
import com.example.a76780.himalaya.base.BaseApplication;
import com.example.a76780.himalaya.interfaces.IPlayCallback;
import com.example.a76780.himalaya.interfaces.IPlayerPresenter;
import com.example.a76780.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {


    private List<IPlayCallback> mIPlayerCallbacks=new ArrayList<>();
    private static final String TAG = "PlayerPresenter";
    private XmPlayerManager mPlayerManager;
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_INDEX=0;
    private int mCurrentIndex=0;
    private final SharedPreferences mPlayModSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode=XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private boolean mIsReverse=false;

//    1.默认的是列表播放：PLAY_MODEL_LIST
//    2.列表循环：        PLAY_MODEL_LIST_LOOP
//    3.随机播放：        PLAY_MODEL_RANDOM
//    4.单曲循环：        PLAY_MODEL_SINGLE_LOOP

    public static final int PLAY_MODEL_LIST_INT=0;
    public static final int PLAY_MODEL_LIST_LOOP_INT=1;
    public static final int PLAY_MODEL_RANDOM_INT=2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT=3;

    //sp's key and name
    public static final String PLAY_MODE_SP_NAME="PlayMod";
    public static final String PLAY_MODE_SP_KEY="currentPlayMode";
    private int modeIndex;
    private int mCurrentProgressPosition=0;
    private int mProgressDuration=0;

    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
        mPlayModSp = BaseApplication.getAppContext().getSharedPreferences("PLAY_MODE_SP_NAME", Context.MODE_PRIVATE);

    }
    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter(){
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class){
                if (sPlayerPresenter == null) {
                    sPlayerPresenter=new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet=false;
    public void setPlayList(List<Track> list,int playIndex){
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list,playIndex);
            isPlayListSet=true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex=playIndex;
        }else {
                LogUtil.d(TAG,"mPlayManager is null");
            }
        }





    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放前一个节目
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放下一个节目
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    /**
     * 判断是否有播放列表
     * @return
     */
    public boolean hasPlayList(){

        return isPlayListSet;
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode=mode;
            mPlayerManager.setPlayMode(mode);

            for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            SharedPreferences.Editor edit=mPlayModSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getInByPlayMode(mode));
        }
        //保存到sp里头去
        SharedPreferences.Editor editor=mPlayModSp.edit();
        editor.putInt(PLAY_MODE_SP_KEY,getInByPlayMode(mode));
        editor.commit();

    }
    private int getInByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }
    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch (index){
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }

    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第index位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }

    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);

    }

    @Override
    public boolean isPlaying() {
       //返回当前是否正在播放
        return  mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //把播放列表翻转
        List<Track> playList=mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse=!mIsReverse;

        //第一个参数是播放列表，第二个参数是开始播放的下标
        //新的下标=总的内容个数-1-当前下标
        mCurrentIndex=playList.size()-1-mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);
        //更新UI
        mCurrentTrack= (Track) mPlayerManager.getCurrSound();
        for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //1.要获取到专辑的列表内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                //2.把专辑内容设置给播放器
                List<Track> tracks=trackList.getTracks();
                if (trackList != null&&tracks.size()>0) {
                    mPlayerManager.setPlayList(tracks,DEFAULT_PLAY_INDEX);
                    isPlayListSet=true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex=DEFAULT_PLAY_INDEX;
                }

            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(), "请求数据错误", Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);

        //3.播放了..
    }

    @Override
    public void registerViewCallback(IPlayCallback iPlayCallback) {
        //通知当前的节目
        iPlayCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        iPlayCallback.onProgressChange(mCurrentProgressPosition,mProgressDuration);
        //更新状态
        handlePlayState(iPlayCallback);
        //从sp里头拿
        modeIndex = mPlayModSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode=getModeByInt(modeIndex);

        iPlayCallback.onPlayModeChange(mCurrentPlayMode);
        if (!mIPlayerCallbacks.contains(iPlayCallback)) {
            mIPlayerCallbacks.add(iPlayCallback);
        }
    }

    private void handlePlayState(IPlayCallback iPlayCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        //根据状态调用接口的方法
        if (PlayerConstants.STATE_STARTED==playerStatus) {
            iPlayCallback.onPlayStart();
        }else {
            iPlayCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayCallback iPlayCallback) {

    }

    //==============广告相关的回调方法 start

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo...");

    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdaInfo...");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering...");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering...");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds...");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds...");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG,"onError what = >"+what+"extra = >"+extra);
    }

    //==========广告相关的回调方法end=========

    //
    //==========播放器相关的回调方法start=========
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart...");
        for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop...");
        for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete...");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus()==PlayerConstants.STATE_PREPARED) {
            //播放器准备完了，可以去播放了
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared...");
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG,"onSoundSwitch...");

        //curModel代表的是当前播放的内容
        //如果通过getKind()方法来获取他是什么类型
        //track表示track类型
        //第一种写法：不推荐
        // if ("track".equals(curModel.getKind())) {
        // Track currentTrack=(Track) curModel;
        //LogUtil.d(TAG,"title==>"+currentTrack.getTrackTitle());
        // }

        //第二种写法
        mCurrentIndex=mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack=(Track)curModel;
            mCurrentTrack=currentTrack;
            //更新UI
            for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            }
        }
        mCurrentIndex=mPlayerManager.getCurrentIndex();
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart...");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop...");
    }

    @Override
    public void onBufferProgress(int i) {
        LogUtil.d(TAG,"onBufferProgress...");
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        this.mCurrentProgressPosition=currPos;
        this.mProgressDuration=duration;
        //单位是毫秒
        for (IPlayCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos,duration);
        }
        LogUtil.d(TAG,"onPlayProgress..."+currPos+"duration-->"+duration);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"e --->"+e);
        return false;
    }
    //============播放器相关的回调方法start================
}
