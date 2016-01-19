package com.github.chaossss.pianoview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaos on 2016/1/19.
 */
public class PianoAdapter extends BaseAdapter {
    private float itemSize;
    private PianoView mRhythmLayout;
    private List<String> iconUrlList;

    public PianoAdapter(PianoView rhythmLayout) {
        this.mRhythmLayout = rhythmLayout;
        this.iconUrlList = new ArrayList<>();
    }

    @Override
    public Object getItem(int position) {
        return iconUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<String> getIconUrlList() {
        return iconUrlList;
    }

    public void addIconUrlList(List<String> iconUrlList) {
        this.iconUrlList.addAll(iconUrlList);
    }

    public void resetIconUrlList(List<String> iconUrlList){
        this.iconUrlList = iconUrlList;
    }

    public int getCount() {
        return iconUrlList == null ? 0 : iconUrlList.size();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Context context = parent.getContext();

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_piano_holder, parent, false);
            convertView.setLayoutParams(new RelativeLayout.LayoutParams((int) itemSize, context.getResources().getDimensionPixelSize(R.dimen.rhythm_item_height)));
            convertView.setTranslationY(itemSize);
            holder = new ViewHolder(convertView);

            int iconSize = (int) itemSize - 2 * context.getResources().getDimensionPixelSize(R.dimen.rhythm_icon_margin);
            ViewGroup.LayoutParams iconParams = holder.icon.getLayoutParams();
            iconParams.width = iconSize;
            iconParams.height = iconSize;
            holder.icon.setLayoutParams(iconParams);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Glide.with(parent.getContext()).load(iconUrlList.get(position)).centerCrop().into(holder.icon);

        return convertView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.mRhythmLayout.invalidateData();
    }

    /**
     * 设置每个item的宽度
     */
    public void setItemSize(float itemSize) {
        this.itemSize = itemSize;
    }

    private class ViewHolder{
        RoundedImageView icon;

        public ViewHolder(View root) {
            icon = (RoundedImageView) root.findViewById(R.id.image_icon);
        }
    }
}
