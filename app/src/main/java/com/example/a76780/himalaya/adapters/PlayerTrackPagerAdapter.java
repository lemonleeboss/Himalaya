package com.example.a76780.himalaya.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.a76780.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPagerAdapter extends PagerAdapter {

    private List<Track> mDate=new ArrayList<>();

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView=LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager,container,false);
        container.addView(itemView);
        //设置数据
        //找到控件
        ImageView item=itemView.findViewById(R.id.track_pager_item);
        //设置图片
        Track track=mDate.get(position);
        String coverUrlLarge=track.getCoverUrlLarge();
        Picasso.with(container.getContext()).load(coverUrlLarge).into(item);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mDate.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    public void setData(List<Track> list) {
        mDate.clear();
        mDate.addAll(list);
        notifyDataSetChanged();
    }
}
