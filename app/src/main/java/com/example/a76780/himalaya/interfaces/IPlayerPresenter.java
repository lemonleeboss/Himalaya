package com.example.a76780.himalaya.interfaces;

import com.example.a76780.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayCallback> {
    /**
     * 播放
     */
    void play();


    /**
     * 暂停
     */
    void pause();


    /**
     * 停止播放
     */
    void stop();


    /**
     * 上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行播放
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 把播放器列表内容翻转
     */
    void reversePlayList();

    /**
     * 播放专辑的第一首节目
     * @param id
     */
    void playByAlbumId(long id);

}
