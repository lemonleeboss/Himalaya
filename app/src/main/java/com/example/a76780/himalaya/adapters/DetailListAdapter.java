package com.example.a76780.himalaya.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a76780.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {
    private List<Track> mDetailData=new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mUpdateDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat=new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener=null;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, final int position) {
        //找到控件
        final View itemView=holder.itemView;
        //顺序Id
        TextView orderTv=itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv=itemView.findViewById(R.id.detail_item_title);
        //播放次数
        TextView playCountTV=itemView.findViewById(R.id.detail_item_play_count);
        //时长
        TextView durationTv=itemView.findViewById(R.id.detail_item_duration);
        //更新日期
        TextView updateDeteTv=itemView.findViewById(R.id.detail_item_update_time);

        //设置数据
        Track track=mDetailData.get(position);
        orderTv.setText((position+1)+"");
        titleTv.setText(track.getTrackTitle());
        playCountTV.setText(track.getPlayCount()+"");

        int durationMil=track.getDuration()*1000;
        String duration=mDurationFormat.format(durationMil);
        durationTv.setText(duration);
        String updateTimeText= mUpdateDateFormat.format(track.getUpdatedAt());
        updateDeteTv.setText(updateTimeText);
        //设置item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    //参数需要有列表和位置
                    mItemClickListener.onItemClick(mDetailData,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //消除原来的数据
        mDetailData.clear();
        //添加新的数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }
    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener=listener;
    }
    public interface ItemClickListener{
        void  onItemClick(List<Track> detailData, int position);
    }
}
