package com.example.a76780.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.a76780.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class IndicatorAdapter extends CommonNavigatorAdapter {
    private final String[] mTitles;
    private OnIndicatorTapClickListener mOnTabClickListener;

    public IndicatorAdapter(Context context) {

        mTitles = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if (mTitles != null) {
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中的颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(18);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitles[index]);
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换viewPagerd的内容，如果index不一样的话
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClick(index);
                }
            }
        });
        //把这个建好的view返回去
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
    }
    public void serOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnTabClickListener=listener;
    }

    public interface OnIndicatorTapClickListener{
        void onTabClick(int index);
    }

}
