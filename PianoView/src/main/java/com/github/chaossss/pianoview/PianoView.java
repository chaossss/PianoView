package com.github.chaossss.pianoview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom view shows a piano keyboard
 * Created by chaos on 2016/1/22.
 */
public class PianoView extends HorizontalScrollView {
    private Context context;
    private Handler handler;
    private ShiftMonitorTimer mTimer;
    private LinearLayout itemWrapper;
    private PianoAdapter pianoAdapter;
    private PianoItemListener pianoItemListener;
    
    //keyboard item's width
    private float itemWidth;

    private int screenWidth;
    private int edgeShiftSize;

    //uses to show item's animation
    private int intervalHeight;

    //current selected item's position
    private int currentItemPosition;

    //max translation height for item
    private int maxTranslationHeight;

    private int scrollStartDelayTime;
    private int lastDisplayItemPosition;

    private long fingerDownTime;

    public PianoView(Context context) {
        this(context, null);
    }

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        screenWidth = localDisplayMetrics.widthPixels;
        itemWidth = screenWidth / 7;
        //keyboard item's max height
        maxTranslationHeight = (int) itemWidth;
        intervalHeight = (maxTranslationHeight / 6);
        edgeShiftSize = getResources().getDimensionPixelSize(R.dimen.piano_edge_shift_size);
        currentItemPosition = -1;
        lastDisplayItemPosition = -1;
        scrollStartDelayTime = 0;
        fingerDownTime = 0;
        handler = new Handler();
        mTimer = new ShiftMonitorTimer();
        mTimer.startMonitor();

        setHorizontalScrollBarEnabled(false);
    }

    public void setAdapter(PianoAdapter adapter) {
        this.pianoAdapter = adapter;

        if (itemWrapper == null) {
            itemWrapper = new LinearLayout(context);
            this.addView(itemWrapper);
        }

        pianoAdapter.setItemWidth(itemWidth);
        for (int i = 0; i < this.pianoAdapter.getCount(); i++) {
            itemWrapper.addView(pianoAdapter.getView(i, null, null));
        }
    }


    public void invalidateData() {
        int childCount = this.itemWrapper.getChildCount();
        if (childCount < this.pianoAdapter.getCount())
            for (int i = childCount; i < this.pianoAdapter.getCount(); i++)
                this.itemWrapper.addView(this.pianoAdapter.getView(i, null, null));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mTimer.monitorTouchPosition(ev.getX(), ev.getY());
                updateItemHeight(ev.getX());
                break;

            case MotionEvent.ACTION_DOWN:
                mTimer.monitorTouchPosition(ev.getX(), ev.getY());
                fingerDownTime = System.currentTimeMillis();
                updateItemHeight(ev.getX());
                if (pianoItemListener != null)
                    pianoItemListener.onStartSwipe();
                break;

            case MotionEvent.ACTION_UP:
                actionUp();
                break;
        }
        return true;
    }

    /**
     * When finger up, other icon down and reset their position
     */
    private void actionUp() {
        mTimer.monitorTouchPosition(-1.0F, -1.0F);
        if (currentItemPosition < 0) {
            return;
        }

        final List<View> viewList = getVisibleViews();

        int size = viewList.size();
        int firstPosition = getFirstVisibleItemPosition();
        int lastPosition = firstPosition + currentItemPosition;

        if (size > currentItemPosition) {
            viewList.remove(currentItemPosition);
        }

        if (firstPosition - 1 >= 0) {
            viewList.add(itemWrapper.getChildAt(firstPosition - 1));
        }

        if (lastPosition + 1 <= itemWrapper.getChildCount()) {
            viewList.add(itemWrapper.getChildAt(lastPosition + 1));
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i = 0; i < viewList.size(); i++) {
                    View downView = viewList.get(i);
                    shootDownItem(downView, true);
                }
            }
        }, 200L);

        if (pianoItemListener != null)
            pianoItemListener.onPianoItemSelected(lastPosition);
        currentItemPosition = -1;
    }

    private void updateItemHeight(float scrollX) {
        List<View> viewList = getVisibleViews();
        int position = (int) (scrollX / itemWidth);
        if (position == currentItemPosition || position >= itemWrapper.getChildCount())
            return;
        currentItemPosition = position;
        makeItems(position, viewList);
    }

    /**
     * Calculates every icon's height needed and start animation
     */
    private void makeItems(int fingerPosition, List<View> viewList) {
        if (fingerPosition >= viewList.size()) {
            return;
        }

        for (int i = 0; i < viewList.size(); i++) {
            int translationY = Math.min(Math.max(Math.abs(fingerPosition - i) * intervalHeight, 10), maxTranslationHeight);
            updateItemHeightAnimator(viewList.get(i), translationY);
        }
    }

    private List<View> getVisibleViews() {
        return getVisibleViews(false, false);
    }

    private List<View> getVisibleViews(boolean isForward, boolean isBackward) {
        List<View> visibleViews = new ArrayList<>();

        if (this.itemWrapper == null)
            return visibleViews;

        int firstPosition = getFirstVisibleItemPosition();
        int lastPosition = firstPosition + 7;

        if (itemWrapper.getChildCount() < 7) {
            lastPosition = itemWrapper.getChildCount();
        }

        if ((isForward) && (firstPosition > 0))
            firstPosition--;

        if ((isBackward) && (lastPosition < itemWrapper.getChildCount()))
            lastPosition++;

        for (int i = firstPosition; i < lastPosition; i++)
            visibleViews.add(itemWrapper.getChildAt(i));

        return visibleViews;
    }

    public int getFirstVisibleItemPosition() {
        if (itemWrapper == null) {
            return 0;
        }

        for (int i = 0; i < itemWrapper.getChildCount(); i++) {
            View view = itemWrapper.getChildAt(i);
            if (getScrollX() < view.getX() + itemWidth / 2.0F)
                return i;
        }
        return 0;
    }

    /**
     * Timer uses to show animation
     */
    class ShiftMonitorTimer extends Timer {
        private TimerTask timerTask;
        private boolean canShift = false;
        private float x;

        void monitorTouchPosition(float x, float y) {
            this.x = x;
            if ((x < 0.0F) || ((x > edgeShiftSize) && (x < screenWidth - edgeShiftSize)) || (y < 0.0F)) {
                fingerDownTime = System.currentTimeMillis();
                this.canShift = false;
            } else {
                this.canShift = true;
            }
        }

        void startMonitor() {
            if (this.timerTask == null) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        long duration = System.currentTimeMillis() - fingerDownTime;

                        if (canShift && duration > 1000) {
                            int firstPosition = getFirstVisibleItemPosition();
                            int toPosition = 0;
                            boolean isForward = false;
                            boolean isBackward = false;
                            final List<View> localList;

                            if (x <= edgeShiftSize && x >= 0.0F) {
                                if (firstPosition - 1 >= 0) {
                                    currentItemPosition = 0;
                                    toPosition = firstPosition - 1;
                                    isForward = true;
                                    isBackward = false;

                                }
                            } else if (x > screenWidth - edgeShiftSize) {
                                if (getSize() >= 1 + (firstPosition + 7)) {
                                    currentItemPosition = 7;
                                    toPosition = firstPosition + 1;
                                    isForward = false;
                                    isBackward = true;
                                }
                            }

                            if (isForward || isBackward) {
                                localList = getVisibleViews(isForward, isBackward);
                                final int finalToPosition = toPosition;
                                handler.post(new Runnable() {
                                    public void run() {
                                        makeItems(currentItemPosition, localList);
                                        scrollToPosition(finalToPosition, 200, 0, true);
                                    }
                                });
                            }
                        }
                    }
                };
            }
            schedule(timerTask, 200L, 250L);
        }
    }

    public int getSize() {
        return itemWrapper == null ? 0 : itemWrapper.getChildCount();
    }

    public View getItemView(int position) {
        return itemWrapper.getChildAt(position);
    }

    public Animator scrollToPosition(int position, int duration, int delayTime, boolean isStart) {
        int viewX = (int) getItemView(position).getX();
        return smoothScrollX(viewX, duration, delayTime, isStart);
    }

    public Animator scrollToPosition(int position, int delayTime, boolean isStart) {
        int viewX = (int) getItemView(position).getX();
        return smoothScrollX(viewX, 300, delayTime, isStart);
    }

    private Animator smoothScrollX(int position, int duration, int delayTime, boolean isStart) {
        return AnimatorUtils.moveScrollViewToX(this, position, duration, delayTime, isStart);
    }

    private void updateItemHeightAnimator(View view, int translationY) {
        if (view != null)
            AnimatorUtils.showUpAndDownBounce(view, translationY, 180, true, true);
    }

    public Animator shootDownItem(int viewPosition, boolean isStart) {
        if ((viewPosition >= 0) && (itemWrapper != null) && (getSize() > viewPosition))
            return shootDownItem(getItemView(viewPosition), isStart);
        return null;
    }

    public Animator shootDownItem(View view, boolean isStart) {
        if (view != null)
            return AnimatorUtils.showUpAndDownBounce(view, maxTranslationHeight, 350, isStart, true);
        return null;
    }

    public Animator bounceUpItem(int viewPosition, boolean isStart) {
        if (viewPosition >= 0)
            return bounceUpItem(getItemView(viewPosition), isStart);
        return null;
    }

    public Animator bounceUpItem(View view, boolean isStart) {
        if (view != null)
            return AnimatorUtils.showUpAndDownBounce(view, 10, 350, isStart, true);
        return null;
    }

    public void setPianoItemListenerListener(PianoItemListener pianoItemListener) {
        this.pianoItemListener = pianoItemListener;
    }

    public void setScrollPianoStartDelayTime(int scrollStartDelayTime) {
        this.scrollStartDelayTime = scrollStartDelayTime;
    }

    public float getPianoItemWidth() {
        return itemWidth;
    }

    public void showPianoAtPosition(int position) {
        if (this.lastDisplayItemPosition == position)
            return;

        Animator scrollAnimator;
        Animator bounceUpAnimator;
        Animator shootDownAnimator;

        if ((this.lastDisplayItemPosition < 0) || (pianoAdapter.getCount() <= 7) || (position <= 3)) {
            scrollAnimator = scrollToPosition(0, scrollStartDelayTime, false);
        } else if (pianoAdapter.getCount() - position <= 3) {
            scrollAnimator = scrollToPosition(pianoAdapter.getCount() - 7, scrollStartDelayTime, false);
        } else {
            scrollAnimator = scrollToPosition(position - 3, scrollStartDelayTime, false);
        }

        bounceUpAnimator = bounceUpItem(position, false);
        shootDownAnimator = shootDownItem(lastDisplayItemPosition, false);
        AnimatorSet animatorSet1 = new AnimatorSet();

        if (bounceUpAnimator != null) {
            animatorSet1.playTogether(bounceUpAnimator);
        }

        if (shootDownAnimator != null) {
            animatorSet1.playTogether(shootDownAnimator);
        }

        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playSequentially(scrollAnimator, animatorSet1);
        animatorSet2.start();
        lastDisplayItemPosition = position;
    }
}