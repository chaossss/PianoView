package com.github.chaossss.pianoview;

/**
 * Created by chaos on 2016/1/19.
 */
public interface PianoItemListener {
    void onRhythmItemChanged(int paramInt);

    void onSelected(int paramInt);

    void onStartSwipe();
}
