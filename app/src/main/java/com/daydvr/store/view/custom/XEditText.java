package com.daydvr.store.view.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/*
 * Copyright (C) 2018 3ivr. All rights reserved.
 *
 * 加强版的EditText,可以响应DrawableLeft 和 DrawableRight的点击事件
 * 要实现响应点击,先设置setDrawableListener
 * @author: Jason(Liu ZhiCheng)
 * @mail  : jason@3ivr.cn
 * @date  : 2018/01/09 14:54
 */

public class XEditText extends android.support.v7.widget.AppCompatEditText {

    private DrawableLeftListener mLeftListener ;
    private DrawableRightListener mRightListener ;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;


    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XEditText(Context context) {
        super(context);
    }

    public void setDrawableLeftListener(DrawableLeftListener listener) {
        this.mLeftListener = listener;
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public interface DrawableLeftListener {
        public void onDrawableLeftClick(View view) ;
    }

    public interface DrawableRightListener {
        public void onDrawableRightClick(View view) ;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mRightListener != null) {
                    Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT] ;
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        mRightListener.onDrawableRightClick(this) ;
                        return true ;
                    }
                }

                if (mLeftListener != null) {
                    Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT] ;
                    if (drawableLeft != null && event.getRawX() <= (getLeft() + drawableLeft.getBounds().width())) {
                        mLeftListener.onDrawableLeftClick(this) ;
                        return true ;
                    }
                }
                break;

                default:
                    break;
        }

        return super.onTouchEvent(event);
    }
}
