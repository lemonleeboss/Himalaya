package com.example.a76780.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;

import com.example.a76780.himalaya.base.BaseApplication;
import com.example.a76780.himalaya.interfaces.ISubDao;
import com.example.a76780.himalaya.interfaces.ISubDaoCallback;
import com.example.a76780.himalaya.utils.Constants;
import com.example.a76780.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao {
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private static final String TAG ="SubscriptionDao" ;
    private final XimalayaDBHelper mXimalayaDBHelper;
    private ISubDaoCallback mCallback=null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());

    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback=callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db=null;
        boolean isAddSuccess=false;
        try{
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues=new ContentValues();
            //封装数据
            contentValues.put(Constants.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_COVER_URL,album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID,album.getId());

            //插入数据
            db.insert(Constants.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isAddSuccess=true;

        }catch (Exception e){
            e.printStackTrace();
            isAddSuccess=false;
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onAddResult(isAddSuccess);
            }

        }

    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db=null;
        boolean isDeleteSuccess=false;
        try{
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.d(TAG,"delete==>"+delete);
            db.setTransactionSuccessful();
            isDeleteSuccess=true;

        }catch (Exception e){
            e.printStackTrace();
            isDeleteSuccess=false;

        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onDelResult(isDeleteSuccess);
            }

        }

    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db=null;
        List<Album> result=new ArrayList<>();
        try{
            db = mXimalayaDBHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_AUTHOR_NAME, null, null, null, null, null, null);
            //封装数据
            while (query.moveToNext()) {
                Album album=new Album();
                //封面图片
                String coverURL = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverURL);

                //
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);

                //
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);

                //
                int tracksCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);

                //
                int playCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);

                //
                int albumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(playCount);
                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer=new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);
                result.add(album);
            }
            //把数据通知出去
            if (mCallback != null) {
                mCallback.onSubListLoaded(result);
            }
            query.close();
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

        }
    }
}
