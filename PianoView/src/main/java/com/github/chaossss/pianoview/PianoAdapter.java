package com.github.chaossss.pianoview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by chaos on 2016/1/19.
 */
public class PianoAdapter extends BaseAdapter {
    /**
     * item的宽度
     */
    private float itemWidth;
    /**
     * 数据源
     */
    private List<Card> mCardList;

    private PianoView mRhythmLayout;

    public PianoAdapter(PianoView rhythmLayout, List<Card> cardList) {
        this.mRhythmLayout = rhythmLayout;
        this.mCardList = new ArrayList();
        this.mCardList.addAll(cardList);
    }

    public List<Card> getCardList() {
        return this.mCardList;
    }

    public void addCardList(List<Card> cardList) {
        mCardList.addAll(cardList);
    }

    public int getCount() {
        return this.mCardList.size();
    }

    public Object getItem(int position) {
        return this.mCardList.get(position);
    }

    public long getItemId(int paramInt) {
        return (this.mCardList.get(paramInt)).getUid();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        //设置item布局的大小以及Y轴的位置
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_piano_holder, parent, false);
        root.setLayoutParams(new RelativeLayout.LayoutParams((int) itemWidth, context.getResources().getDimensionPixelSize(R.dimen.rhythm_item_height)));
        root.setTranslationY(itemWidth);

        int iconSize = (int) itemWidth - 2 * context.getResources().getDimensionPixelSize(R.dimen.rhythm_icon_margin);
        ImageView imageIcon = (ImageView) root.findViewById(R.id.image_icon);
        ViewGroup.LayoutParams iconParams = imageIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        imageIcon.setLayoutParams(iconParams);
        //设置背景图片
        imageIcon.setBackgroundResource(AppUtils.getDrawableIdByName(context, mCardList.get(position).getIconUrl()));

        return root;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.mRhythmLayout.invalidateData();
    }

    public void setCardList(List<Card> paramList) {
        this.mCardList = paramList;
    }

    /**
     * 设置每个item的宽度
     */
    public void setItemWidth(float width) {
        this.itemWidth = width;
    }
}
