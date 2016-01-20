package com.github.chaossss.pianoview;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PianoAdapter extends BaseAdapter {
    private float itemWidth;
    private Context context;
    private List<String> iconUrlList;
    private PianoView pianoView;

    public PianoAdapter(Context context, PianoView pianoView) {
        this.context = context;
        this.pianoView = pianoView;
        iconUrlList = new ArrayList<>();
    }

    public void addIconUrlList(List<String> iconUrlList) {
        this.iconUrlList.addAll(iconUrlList);
        this.notifyDataSetChanged();
    }

    public void resetIconUrlList(List<String> iconUrlList){
        this.iconUrlList = new ArrayList<>(iconUrlList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return iconUrlList == null ? 0 : iconUrlList.size();
    }

    public Object getItem(int position) {
        return iconUrlList.get(position);
    }

    public long getItemId(int paramInt) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int layoutSize = (int)itemWidth;
        int itemHeight = (int) pianoView.getPianoItemWidth() + (int) TypedValue.applyDimension(1, 10.0F, context.getResources().getDisplayMetrics());
        int margin = context.getResources().getDimensionPixelSize(R.dimen.rhythm_icon_margin);
        int imgSize = layoutSize - 2 * context.getResources().getDimensionPixelSize(R.dimen.rhythm_icon_margin);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_piano_holder, parent, false);
            convertView.setLayoutParams(new RelativeLayout.LayoutParams(layoutSize, itemHeight));
            convertView.setTranslationY(layoutSize);

            holder = new ViewHolder(convertView);
            holder.setImgParams(imgSize, margin);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Glide.with(context)
                .load(iconUrlList.get(position))
                .centerCrop()
                .into(holder.img);

        return convertView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.pianoView.invalidateData();
    }

    public void setItemWidth(float width) {
        this.itemWidth = width;
    }

    private class ViewHolder{
        RoundedImageView img;

        public ViewHolder(View itemView) {
            img = (RoundedImageView) itemView.findViewById(R.id.holder_piano_img);
        }

        public void setImgParams(int size, int margin){
            RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams)img.getLayoutParams();
            imgParams.width = size;
            imgParams.height = size;
            imgParams.topMargin = margin;
            imgParams.leftMargin = margin;
            imgParams.rightMargin = margin;
            imgParams.bottomMargin = margin;

            img.setLayoutParams(imgParams);
        }
    }
}