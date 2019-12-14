package com.example.a76780.himalaya.base;

public interface IBasePresenter<T> {
    /**
     * 这个方法用于注册UI的回调
     */
    void registerViewCallback(T t);
    /**
     * 取消UI的回调注册
     */
    void unRegisterViewCallback(T t);
}
