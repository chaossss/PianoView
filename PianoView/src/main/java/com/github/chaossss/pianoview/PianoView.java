package com.github.chaossss.pianoview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chaos on 2016/1/19.
 */
public class PianoView extends HorizontalScrollView {

    private Context mContext;

    /**
     * 适配器为这个自定义控件提供子控件
     */
    private PianoAdapter mAdapter;

    private Handler mHandler;

    private ShiftMonitorTimer mTimer;

    /**
     * 监听器，监听手指离开屏幕时的位置
     */
    private PianoItemListener mListener;

    /**
     * ScrollView的子控件
     */
    private LinearLayout mLinearLayout;
    /**
     * item的宽度，为屏幕的1/7
     */
    private float mItemSize;
    /**
     * item位移最大的高度
     */
    private int mMaxTranslationHeight;
    /**
     * item位移的单位，以这个值为基础开始阶梯式位移动画
     */
    private int mIntervalHeight;
    /**
     * 当前被选中的的Item
     */
    private int mCurrentItemPosition;
    /**
     * 左右两边的尺寸
     */
    private int mEdgeSizeForShiftRhythm;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 按下屏幕时的时间
     */
    private long mFingerDownTime;
    /**
     * ScrollView滚动动画延迟执行的时间
     */
    private int mScrollStartDelayTime;
    /**
     * 上一次所选中的item的位置
     */
    private int mLastDisplayItemPosition;

    public PianoView(Context context) {
        this(context, null);
    }

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        //获得屏幕宽度
        mScreenWidth = AppUtils.getScreenDisplayMetrics(mContext).widthPixels;
        //获取Item的宽度，为屏幕的七分之一
        mItemSize = mScreenWidth / 7;
        //钢琴按钮的最大高度
        mMaxTranslationHeight = (int) mItemSize;
        mIntervalHeight = (mMaxTranslationHeight / 6);
        //初始化
        mEdgeSizeForShiftRhythm = getResources().getDimensionPixelSize(R.dimen.rhythm_edge_size_for_shift);
        mCurrentItemPosition = -1;
        mLastDisplayItemPosition = -1;
        mScrollStartDelayTime = 0;
        mFingerDownTime = 0;
        mHandler = new Handler();
        mTimer = new ShiftMonitorTimer();
        mTimer.startMonitor();
    }

    public void setAdapter(PianoAdapter adapter) {
        this.mAdapter = adapter;
        //获取HorizontalScrollView下的LinearLayout控件
        if (mLinearLayout == null) {
            mLinearLayout = (LinearLayout) getChildAt(0);
        }
        //循环获取adapter中的View，设置item的宽度并且add到mLinearLayout中
        mAdapter.setItemSize(mItemSize);
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            mLinearLayout.addView(mAdapter.getView(i, null, null));
        }
    }


    public void invalidateData() {
        int childCount = this.mLinearLayout.getChildCount();
        if (childCount < this.mAdapter.getCount())
            for (int i = childCount; i < this.mAdapter.getCount(); i++)
                this.mLinearLayout.addView(this.mAdapter.getView(i, null, null));
    }

    /**
     * 触摸监听
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE://移动
                mTimer.monitorTouchPosition(ev.getX(), ev.getY());
                updateItemHeight(ev.getX());
                break;
            case MotionEvent.ACTION_DOWN://按下
                mTimer.monitorTouchPosition(ev.getX(), ev.getY());
                //得到按下时的时间戳
                mFingerDownTime = System.currentTimeMillis();
                //更新钢琴按钮的高度
                updateItemHeight(ev.getX());
                if (mListener != null)
                    mListener.onStartSwipe();
                break;
            case MotionEvent.ACTION_UP://抬起
                actionUp();
                break;
        }

        return true;
    }

    /**
     * 手指抬起时将其他小图标落下，重置到初始位置
     */
    private void actionUp() {
        mTimer.monitorTouchPosition(-1.0F, -1.0F);
        if (mCurrentItemPosition < 0) {
            return;
        }
        int firstPosition = getFirstVisibleItemPosition();
        int lastPosition = firstPosition + mCurrentItemPosition;
        final List viewList = getVisibleViews();
        int size = viewList.size();
        //将当前小图标从要落下的ViewList中删除
        if (size > mCurrentItemPosition) {
            viewList.remove(mCurrentItemPosition);
        }
        if (firstPosition - 1 >= 0) {
            viewList.add(mLinearLayout.getChildAt(firstPosition - 1));
        }
        if (lastPosition + 1 <= mLinearLayout.getChildCount()) {
            viewList.add(mLinearLayout.getChildAt(lastPosition + 1));
        }
        //200毫秒后执行动画
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                for (int i = 0; i < viewList.size(); i++) {
                    View downView = (View) viewList.get(i);
                    shootDownItem(downView, true);
                }
            }
        }, 200L);
        //触发监听
        if (mListener != null)
            mListener.onSelected(lastPosition);
        mCurrentItemPosition = -1;
        //使设备震动
        vibrate(20L);
    }

    //更新小图标的高度
    private void updateItemHeight(float scrollX) {
        //得到屏幕上可见的7个小图标的视图
        List viewList = getVisibleViews();
        //当前手指所在的item
        int position = (int) (scrollX / mItemSize);
        //如果手指位置没有发生变化或者大于childCount的则跳出方法不再继续执行
        if (position == mCurrentItemPosition || position >= mLinearLayout.getChildCount())
            return;
        mCurrentItemPosition = position;
        makeItems(position, viewList);
    }

    /**
     * 计算出个个小图标需要的高度并开始动画
     */
    private void makeItems(int fingerPosition, List<View> viewList) {
        if (fingerPosition >= viewList.size()) {
            return;
        }
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            //根据小图标的位置计算出在Y轴需要位移的大小

            int translationY = Math.min(Math.max(Math.abs(fingerPosition - i) * mIntervalHeight, 10), mMaxTranslationHeight);
            //位移动画
            updateItemHeightAnimator(viewList.get(i), translationY);
        }

    }


    /**
     * 得到当前可见的7个小图标
     */
    private List<View> getVisibleViews() {
        ArrayList arrayList = new ArrayList();
        if (mLinearLayout == null)
            return arrayList;
        //当前可见的第一个小图标的位置
        int firstPosition = getFirstVisibleItemPosition();
        //当前可见的最后一个小图标的位置
        int lastPosition = firstPosition + 7;
        if (mLinearLayout.getChildCount() < 7) {
            lastPosition = mLinearLayout.getChildCount();
        }
        //取出当前可见的7个小图标
        for (int i = firstPosition; i < lastPosition; i++)
            arrayList.add(mLinearLayout.getChildAt(i));
        return arrayList;
    }

    /**
     * 获得firstPosition-1 和 lastPosition +1 一集当前可见的7个总共9个小图标
     *
     * @param isForward  是否获取firstPosition - 1 位置的小图标
     * @param isBackward 是否获取lastPosition + 1 位置的小图标
     * @return
     */
    private List<View> getVisibleViews(boolean isForward, boolean isBackward) {
        ArrayList viewList = new ArrayList();
        if (this.mLinearLayout == null)
            return viewList;
        int firstPosition = getFirstVisibleItemPosition();
        int lastPosition = firstPosition + 7;
        if (mLinearLayout.getChildCount() < 7) {
            lastPosition = mLinearLayout.getChildCount();
        }
        if ((isForward) && (firstPosition > 0))
            firstPosition--;
        if ((isBackward) && (lastPosition < mLinearLayout.getChildCount()))
            lastPosition++;
        for (int i = firstPosition; i < lastPosition; i++)
            viewList.add(mLinearLayout.getChildAt(i));

        return viewList;
    }

    /**
     * 得到可见的第一个小图标的位置
     */
    public int getFirstVisibleItemPosition() {
        if (mLinearLayout == null) {
            return 0;
        }
        //获取小图标的数量
        int size = mLinearLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = mLinearLayout.getChildAt(i);
            //当出现小图标的x轴比当前ScrollView的x轴大时，这个小图标就是当前可见的第一个
            if (getScrollX() < view.getX() + mItemSize / 2.0F)
                return i;
        }
        return 0;
    }

    /**
     * 计时器，实现爬楼梯效果
     */
    class ShiftMonitorTimer extends Timer {
        private TimerTask timerTask;
        private boolean canShift = false;
        private float x;
        private float y;

        void monitorTouchPosition(float x, float y) {
            this.x = x;
            this.y = y;
            //当按下位置在第一个后最后一个，或x<0,y<0时，canShift为false，使计时器线程中的代码不能执行
            if ((x < 0.0F) || ((x > mEdgeSizeForShiftRhythm) && (x < mScreenWidth - mEdgeSizeForShiftRhythm)) || (y < 0.0F)) {
                mFingerDownTime = System.currentTimeMillis();
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
                        long duration = System.currentTimeMillis() - mFingerDownTime;
                        //按下时间大于1秒，且按下的是第一个或者最后一个时为true
                        if (canShift && duration > 1000) {
                            int firstPosition = getFirstVisibleItemPosition();
                            int toPosition = 0; //要移动到的小图标的位置
                            boolean isForward = false; //是否获取第firstPosition-1个小图标
                            boolean isBackward = false;//是否获取第lastPosition+1个小图标
                            final List<View> localList;
                            if (x <= mEdgeSizeForShiftRhythm && x >= 0.0F) {//第一个
                                if (firstPosition - 1 >= 0) {
                                    mCurrentItemPosition = 0;
                                    toPosition = firstPosition - 1;
                                    isForward = true;
                                    isBackward = false;

                                }
                            } else if (x > mScreenWidth - mEdgeSizeForShiftRhythm) {//最后一个
                                if (getSize() >= 1 + (firstPosition + 7)) {
                                    mCurrentItemPosition = 7;
                                    toPosition = firstPosition + 1;
                                    isForward = false;
                                    isBackward = true;
                                }
                            }
                            //当按下的是第一个的时候isForward为true，最后一个时isBackward为true
                            if (isForward || isBackward) {
                                localList = getVisibleViews(isForward, isBackward);
                                final int finalToPosition = toPosition;
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        makeItems(mCurrentItemPosition, localList);//设置每个Item的高度
                                        scrollToPosition(finalToPosition, 200, 0, true);//设置ScrollView在x轴的坐标
                                        vibrate(10L);
                                    }
                                });
                            }

                        }

                    }
                };
            }
            //200毫秒之后开始执行，每隔250毫秒执行一次
            schedule(timerTask, 200L, 250L);

        }
    }

    public int getSize() {
        if (mLinearLayout == null) {
            return 0;
        }
        return mLinearLayout.getChildCount();
    }

    public View getItemView(int position) {
        return mLinearLayout.getChildAt(position);
    }

    /**
     * @param position   要移动到的view的位置
     * @param duration   动画持续时间
     * @param startDelay 延迟动画开始时间
     * @param isStart    动画是否开始
     * @return
     */
    public Animator scrollToPosition(int position, int duration, int startDelay, boolean isStart) {
        int viewX = (int) getItemView(position).getX();
        return smoothScrollX(viewX, duration, startDelay, isStart);
    }

    /**
     * ScrollView滚动动画X轴位移
     *
     * @param position   view的位置
     * @param startDelay 延迟动画开始时间
     * @param isStart    动画是否开始
     * @return
     */
    public Animator scrollToPosition(int position, int startDelay, boolean isStart) {
        int viewX = (int) getItemView(position).getX();
        return smoothScrollX(viewX, 300, startDelay, isStart);
    }

    private Animator smoothScrollX(int position, int duration, int startDelay, boolean isStart) {
        return AnimatorUtils.moveScrollViewToX(this, position, duration, startDelay, isStart);
    }

    /**
     * 根据给定的值进行Y轴位移的动画
     *
     * @param view
     * @param translationY
     */
    private void updateItemHeightAnimator(View view, int translationY) {
        if (view != null)
            AnimatorUtils.showUpAndDownBounce(view, translationY, 180, true, true);
    }


    /**
     * 位移到Y轴'最低'的动画
     *
     * @param viewPosition view的位置
     * @param isStart      是否开始动画
     * @return
     */
    public Animator shootDownItem(int viewPosition, boolean isStart) {
        if ((viewPosition >= 0) && (mLinearLayout != null) && (getSize() > viewPosition))
            return shootDownItem(getItemView(viewPosition), isStart);
        return null;
    }

    public Animator shootDownItem(View view, boolean isStart) {
        if (view != null)
            return AnimatorUtils.showUpAndDownBounce(view, mMaxTranslationHeight, 350, isStart, true);
        return null;
    }

    /**
     * 位移到Y轴'最高'的动画
     *
     * @param viewPosition view的位置
     * @param isStart      是否开始动画
     * @return
     */
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

    /**
     * 让设备震动
     *
     * @param miss 震动的时间
     */
    private void vibrate(long miss) {
        ((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{0L, miss}, -1);
    }


    public void setRhythmListener(PianoItemListener listener) {
        this.mListener = listener;
    }
    /**
     * 设置滚动动画延迟执行时间
     *
     * @param scrollStartDelayTime 延迟时间毫秒为单位
     */
    public void setScrollRhythmStartDelayTime(int scrollStartDelayTime) {
        this.mScrollStartDelayTime = scrollStartDelayTime;
    }

    /*
     * 得到每个钢琴按钮的宽度
     */
    public float getRhythmItemSize() {
        return mItemSize;
    }


    /**
     * 位移到所选中的item位置，并进行相应的动画
     *
     * @param position 前往的item位置
     */
    public void showRhythmAtPosition(int position) {
        //如果所要移动的位置和上一次一样则退出方法
        if (this.mLastDisplayItemPosition == position)
            return;
        //ScrollView的滚动条位移动画
        Animator scrollAnimator;
        //item的弹起动画
        Animator bounceUpAnimator;
        //item的降下动画
        Animator shootDownAnimator;

        if ((this.mLastDisplayItemPosition < 0) || (mAdapter.getCount() <= 7) || (position <= 3)) {
            //当前要位移到的位置为前3个时或者总的item数量小于7个
            scrollAnimator = scrollToPosition(0, mScrollStartDelayTime, false);
        } else if (mAdapter.getCount() - position <= 3) {
            //当前要位移到的位置为最后3个
            scrollAnimator = scrollToPosition(mAdapter.getCount() - 7, mScrollStartDelayTime, false);
        } else {
            //当前位移到的位置既不是前3个也不是后3个
            scrollAnimator = scrollToPosition(position - 3, mScrollStartDelayTime, false);
        }
        //获取对应item升起动画
        bounceUpAnimator = bounceUpItem(position, false);
        //获取对应item降下动画
        shootDownAnimator = shootDownItem(mLastDisplayItemPosition, false);
        //动画合集 弹起动画和降下动画的组合
        AnimatorSet animatorSet1 = new AnimatorSet();
        if (bounceUpAnimator != null) {
            animatorSet1.playTogether(bounceUpAnimator);
        }
        if (shootDownAnimator != null) {
            animatorSet1.playTogether(shootDownAnimator);
        }
        //3个动画的组合
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playSequentially(new Animator[]{scrollAnimator, animatorSet1});
        animatorSet2.start();
        mLastDisplayItemPosition = position;
    }
}
