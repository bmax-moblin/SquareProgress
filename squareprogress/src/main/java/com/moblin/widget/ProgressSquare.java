package com.moblin.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Shows a cyclic animation without an indication of progress.
 * Custom attribute:
 * color - square shape's color
 */
public class ProgressSquare extends View implements Animator.AnimatorListener {
    private static final int TOTAL_STEPS = 8;
    private ValueAnimator mAnimator;
    private Paint mPaint = new Paint();
    private RectF mBounds = new RectF();
    private Path mPath = new Path();
    private int mDefaultSize, mStepNum, mPadding, mColor,
            mAnimationDuration, mStartDelay, mStrokeWidth;

    /**
     * Constructor that is called when inflating a view from XML.
     * This is called when a view is being constructed from an XML file,
     * supplying attributes that were specified in the XML file.
     */
    public ProgressSquare(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, getResources());
    }

    /** View methods */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = resolveSize(mDefaultSize, widthMeasureSpec);
        int h = resolveSize(mDefaultSize, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(w, h);
        // Bound drawing to a square and account for the padding.
        setupBounds(w, h, size);
        // Travel distance excludes the padding on both sides.
        setupAnimator(size - mPadding * 2f);
        // After everything is set, it's safe to start drawing.
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // The path is changed in each frame.
        mPath.reset();
        // Start from the top left corner.
        mPath.moveTo(mBounds.left, mBounds.top);
        float animatedValue = (float) mAnimator.getAnimatedValue();
        switch (mStepNum) {
            case 0:
                // Drawing CW
                mPath.lineTo(mBounds.left + animatedValue, mBounds.top);
                break;
            case 1:
                // Drawing CW
                mPath.lineTo(mBounds.right, mBounds.top);
                mPath.lineTo(mBounds.right, mBounds.top + animatedValue);
                break;
            case 2:
                // Drawing CW
                mPath.lineTo(mBounds.right, mBounds.top);
                mPath.lineTo(mBounds.right, mBounds.bottom);
                mPath.lineTo(mBounds.right - animatedValue, mBounds.bottom);
                break;
            case 3:
                // Drawing CW
                mPath.lineTo(mBounds.right, mBounds.top);
                mPath.lineTo(mBounds.right, mBounds.bottom);
                mPath.lineTo(mBounds.left, mBounds.bottom);
                mPath.lineTo(mBounds.left, mBounds.bottom - animatedValue);
                break;
            case 4:
                // Drawing CCW
                mPath.lineTo(mBounds.left, mBounds.bottom);
                mPath.lineTo(mBounds.right, mBounds.bottom);
                mPath.lineTo(mBounds.right, mBounds.top);
                mPath.lineTo(mBounds.left + animatedValue, mBounds.top);
                break;
            case 5:
                // Drawing CCW
                mPath.lineTo(mBounds.left, mBounds.bottom);
                mPath.lineTo(mBounds.right, mBounds.bottom);
                mPath.lineTo(mBounds.right, mBounds.top + animatedValue);
                break;
            case 6:
                // Drawing CCW
                mPath.lineTo(mBounds.left, mBounds.bottom);
                mPath.lineTo(mBounds.right - animatedValue, mBounds.bottom);
                break;
            case 7:
                // Drawing CCW
                mPath.lineTo(mBounds.left, mBounds.bottom - animatedValue);
                break;
            default:
        }
        canvas.drawPath(mPath, mPaint);
        // Ask for the next frame.
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.cancel();
    }

    /** Animator Listener */

    @Override
    public void onAnimationStart(Animator animation) {
        // No action
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        // No action
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // No action
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // Increment the step and rotate if needed.
        mStepNum = (mStepNum + 1) % TOTAL_STEPS;
    }

    /** Private methods */

    private void init(AttributeSet attrs, Resources res) {
        mDefaultSize = res.getDimensionPixelSize(R.dimen.progress_square_default_size);
        mStrokeWidth = res.getDimensionPixelSize(R.dimen.progress_square_stroke_width);
        mPadding = res.getDimensionPixelSize(R.dimen.progress_square_padding);
        mAnimationDuration = res.getInteger(android.R.integer.config_mediumAnimTime);
        mStartDelay = res.getInteger(android.R.integer.config_shortAnimTime);
        readColor(attrs);
        setupPaint();
    }

    private void readColor(AttributeSet attrs) {
        TypedArray a = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ProgressSquare, 0, 0);
        mColor = a.getColor(R.styleable.ProgressSquare_color, Color.BLUE);
        a.recycle();
    }

    private void setupPaint() {
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth((float)mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void setupBounds(float width, float height, float size) {
        mBounds.left = (width - size) * 0.5f + mPadding;
        mBounds.top = (height - size) * 0.5f + mPadding;
        mBounds.right = (width + size) * 0.5f - mPadding;
        mBounds.bottom = (height + size) * 0.5f - mPadding;
    }

    private void setupAnimator(float distance) {
        mAnimator = ValueAnimator.ofFloat(0f, distance);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setStartDelay(mStartDelay);
        mAnimator.addListener(this);
    }
}
