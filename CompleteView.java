package cn.miao.tasksdk.custom_view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import cn.miao.tasksdk.R;


public class CompleteView extends View {

    private Paint mPaint;
    private Path path_circle;
    private PathMeasure mMeasure;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private float mAnimatorValue;
    private Animator.AnimatorListener mAnimatorListener;
    private Handler mHandler;
    // 当前的状态(非常重要)
    private State mCurrentState = State.NONE;

    // 默认的动效周期 2s
    private int defaultDuration = 500;
    /**
     * 旋转动画
     */
    private ValueAnimator mRotateAnimator;

    // View 宽高
    private int mViewWidth;
    private int mViewHeight;
    /**
     * 第二段圆弧路径
     */
    private Path path_drop;
    /**
     * 下落动画
     */
    private ValueAnimator dropAnimator;
    private RectF oval;
    private Paint mCirclePaint;

    // 这个视图拥有的状态
    public static enum State {
        NONE,
        ROTATE,
        DROP,
        RIGHTMARK,
    }

    public CompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    private void initAll() {
        initPaint();
        initPath();
        initListener();
        initHandler();
        initAnimator();
        // 进入开始动画
        mCurrentState = State.DROP;
        dropAnimator.start();

    }

    public void strart() {
        mCurrentState = State.DROP;
        dropAnimator.start();
    }

    private void initPaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.WHITE);


        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.sdk_ff26c2f2));
        mPaint.setStrokeWidth(15);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    private void initPath() {

        mMeasure = new PathMeasure();
//        //下落路径
//        path_drop = new Path();
//        path_drop.moveTo(0,-200);
//        path_drop.lineTo(0,100);
//        path_drop.lineTo(0,-100);

        //圆和对号
        path_circle = new Path();
//        // 注意,不要到360度,否则内部会自动优化,测量不能取到需要的数值
//        oval = new RectF(-150, -150, 150, 150);      // 外部圆环
//        path_circle.addArc(oval, -90, -359.9f);
//        path_circle.arcTo(oval, -90, -60f);
//        path_circle.lineTo(-15,30);
//        path_circle.lineTo(30,-30);
//        path_circle2
    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // getHandle发消息通知动画状态更新
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case DROP://旋转状态转下个状态
                        mCurrentState = State.ROTATE;
                        //第二段圆弧开始
                        mRotateAnimator.start();
                        break;
                    case ROTATE://旋转状态转下个状态
                        //第二段圆弧开始
//                        mCurrentState = State.ROTATE2;
//                        //第二段圆弧开始
//                        mRotateAnimator.start();
                        break;
                    case RIGHTMARK:

                        break;

                }
            }
        };
    }

    private void initAnimator() {
        mRotateAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        dropAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);

        mRotateAnimator.addUpdateListener(mUpdateListener);
        dropAnimator.addUpdateListener(mUpdateListener);

        mRotateAnimator.addListener(mAnimatorListener);
        dropAnimator.addListener(mAnimatorListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //取长宽最小值
        if (mViewWidth > mViewHeight) {
            mViewWidth = mViewHeight;
        }
        //外圆半径
        int outside_radius = mViewWidth / 2 - 20;
        //内圆半径
        int inside_radius = mViewWidth / 4;
        //下落
        //下落路径
        path_drop = new Path();
        path_drop.moveTo(0, -outside_radius);
        path_drop.lineTo(0, outside_radius-inside_radius);
        path_drop.lineTo(0, -inside_radius);

        //圆环
        // 注意,不要到360度,否则内部会自动优化,测量不能取到需要的数值
        oval = new RectF(-inside_radius, -inside_radius, inside_radius, inside_radius);      // 外部圆环
        path_circle.addArc(oval, -90, -359.9f);
        path_circle.arcTo(oval, -90, -60f);
        path_circle.lineTo(-inside_radius / 12, inside_radius / 3);
        path_circle.lineTo(inside_radius / 2.5f, -inside_radius / 4.5f);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mViewWidth / 2, mViewHeight / 2);

        canvas.drawColor(Color.parseColor("#0082D7"));
        canvas.drawCircle(0, 0, mViewWidth / 4, mCirclePaint);
        switch (mCurrentState) {

            case NONE:
                canvas.drawPath(path_circle, mPaint);
                break;
            case RIGHTMARK:
//                mMeasure.setPath(path_circle, false);
//                Path dst = new Path();
//                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst, true);
//                canvas.drawPath(dst, mPaint);
                break;
            case ROTATE:
                float start;
                mMeasure.setPath(path_circle, false);
                Path dst = new Path();
                float stop = mMeasure.getLength() * mAnimatorValue;
                if (mAnimatorValue > 0.5) {
                    start = stop - mMeasure.getLength()/8.8f;
                } else {
                    start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * mMeasure.getLength()/8.8));
                }
                mMeasure.getSegment(start, stop, dst, true);
                canvas.drawPath(dst, mPaint);


                //画点
//                float[] p = new float[2];
//                mMeasure.getPosTan(mMeasure.getLength()*mAnimatorValue,p,null);
//                canvas.drawPoint(p[0],p[1],mPaint);
                break;
            case DROP:
                mMeasure.setPath(path_drop, false);
                float[] p = new float[2];
                mMeasure.getPosTan(mMeasure.getLength() * mAnimatorValue, p, null);
                canvas.drawPoint(p[0], p[1], mPaint);
                break;

        }

    }
}
