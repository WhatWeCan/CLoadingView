package com.tjstudy.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 环形的loadingView
 */
public class CLoadingView extends View {

    private int width;
    private int height;
    private Paint mCirclePaint;
    private Paint mTxtPaint;
    private int progress;
    private TypedArray typedArray;
    private float mRingWidth;
    private int mRingBackColor;
    private final int mDefaultBackColor = 0xFF0000FF;
    private int mRingForeColor;
    private final int mDefaultForeColor = 0xFF00FF00;
    private float mCircleRadius;
    private int mProgressTxtColor;
    private final int mDefaultProgressTxtColor = 0xFF444444;
    private float mProgressTxtSize;
    private int mProgressTime;
    private Context mContext;
    private LoadingListener mLoadingListener;

    public CLoadingView(Context context) {
        this(context, null);
    }

    public CLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CLoadingView, defStyleAttr, 0);
        mContext = context;
        initData();
        initPaint();
        doAnimator();
    }

    private void initData() {
        mRingWidth = typedArray.getDimension(R.styleable.CLoadingView_ringWidth, 20);
        mRingBackColor = typedArray.getColor(R.styleable.CLoadingView_ringBackColor, mDefaultBackColor);
        mRingForeColor = typedArray.getColor(R.styleable.CLoadingView_ringForeColor, mDefaultForeColor);
        mProgressTxtColor = typedArray.getColor(R.styleable.CLoadingView_progressTxtColor, mDefaultProgressTxtColor);
        mProgressTxtSize = typedArray.getDimension(R.styleable.CLoadingView_progressTxtSize, 36);
        mProgressTime = typedArray.getInteger(R.styleable.CLoadingView_duration, 6000);
        typedArray.recycle();
    }

    private void initPaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);//去掉锯齿
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {//处理wrap_content的情况
            width = 200;//200px
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {//处理wrap_content的情况
            height = 200;//200px
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        mCircleRadius = (width > height ? height / 2 : width / 2) - mRingWidth;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);
        RectF rectF = new RectF(-mCircleRadius, -mCircleRadius, mCircleRadius, mCircleRadius);
        //1、画圆弧底色
        mCirclePaint.setStrokeWidth(mRingWidth);
        mCirclePaint.setColor(mRingBackColor);
        canvas.drawArc(rectF, -90, 360, false, mCirclePaint);

        //2、画进度文本
        String progressTxt = progress + "%";
        Rect txtBounds = new Rect();
        mTxtPaint.setColor(mProgressTxtColor);
        mTxtPaint.setTextSize(mProgressTxtSize);
        mTxtPaint.getTextBounds(progressTxt, 0, progressTxt.length(), txtBounds);
        //文本的位置
        canvas.drawText(progressTxt, -txtBounds.width() / 2, txtBounds.height() / 2, mTxtPaint);

        //3、画进度圆弧
        //进度计算 360度圆弧=100进度  1进度=3.6度圆弧
        float currentDegree = progress * 3.6f;
        mCirclePaint.setColor(mRingForeColor);
        canvas.drawArc(rectF, -90, currentDegree, false, mCirclePaint);

        if (mLoadingListener != null && progress == 100) {
            mLoadingListener.loadingFinished();
        }
    }

    private void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    private void doAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "progress", 0, 100);
        animator.setDuration(mProgressTime);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 获取圆环的宽度
     *
     * @return mRingWidth
     */
    public float getRingWidth() {
        return dp2px(mRingWidth);
    }

    /**
     * 设置圆环的宽度
     *
     * @param mRingWidth 圆环宽度
     */
    public void setRingWidth(float mRingWidth) {
        this.mRingWidth = px2dp(mRingWidth);
    }

    /**
     * 获取圆环的背景色
     *
     * @return mRingBackColor
     */
    public int getRingBackColor() {
        return mRingBackColor;
    }

    /**
     * 设置圆环的背景色
     *
     * @param mRingBackColor 圆弧背景色
     */
    public void setRingBackColor(int mRingBackColor) {
        this.mRingBackColor = mRingBackColor;
    }

    /**
     * 获取圆环的前景色
     *
     * @return mRingForeColor
     */
    public int getRingForeColor() {
        return mRingForeColor;
    }

    /**
     * 设置圆环的前景色
     *
     * @param mRingForeColor 圆环前景色
     */
    public void setRingForeColor(int mRingForeColor) {
        this.mRingForeColor = mRingForeColor;
    }

    /**
     * 获取进度文本的颜色
     *
     * @return mProgressTxtColor
     */
    public int getProgressTxtColor() {
        return mProgressTxtColor;
    }

    /**
     * 设置进度文本的颜色
     *
     * @param mProgressTxtColor 进度文本颜色
     */
    public void setProgressTxtColor(int mProgressTxtColor) {
        this.mProgressTxtColor = mProgressTxtColor;
    }

    /**
     * 获取进度文本的字体大小
     *
     * @return mProgressTxtSize
     */
    public float getProgressTxtSize() {
        return px2dp(mProgressTxtSize);
    }

    /**
     * 设置进度文本的字体大小
     *
     * @param mProgressTxtSize 进度文本字体大小
     */
    public void setProgressTxtSize(float mProgressTxtSize) {
        this.mProgressTxtSize = dp2px(mProgressTxtSize);
    }

    /**
     * 获取进度加载的时间
     *
     * @return mProgressTime
     */
    public int getProgressTime() {
        return mProgressTime;
    }

    /**
     * 设置进度加载时间
     *
     * @param mProgressTime 进度加载时间
     */
    public void setProgressTime(int mProgressTime) {
        this.mProgressTime = mProgressTime;
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    private int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public int px2dp(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void setLoadingFinishListener(LoadingListener loadingListener) {
        mLoadingListener = loadingListener;
    }

    public interface LoadingListener {
        void loadingFinished();
    }
}
