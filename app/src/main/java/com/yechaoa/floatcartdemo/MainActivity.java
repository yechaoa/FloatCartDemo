package com.yechaoa.floatcartdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ImageView mIvCart;
    private List<String> titles = new ArrayList<>();
    private float startY;//上下滑动的距离
    private int moveDistance;//动画移动的距离
    private boolean isShowFloatImage = true;//标记图片是否显示
    private Timer timer;//计时器
    private long upTime;//记录抬起的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        mListView = findViewById(R.id.list_view);
        mIvCart = findViewById(R.id.iv_cart);
    }

    private void initData() {
        for (int i = 0; i < 50; i++) {
            titles.add("第  -  " + i + "  -  条数据");
        }

        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));

        //控件绘制完成之后再获取其宽高
        mIvCart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //动画移动的距离 屏幕的宽度减去图片距左边的宽度 就是图片距右边的宽度，再加上隐藏的一半
                moveDistance = getScreenWidth() - mIvCart.getRight() + mIvCart.getWidth() / 2;
                //监听结束之后移除监听事件
                mIvCart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                if (System.currentTimeMillis() - upTime < 1000) {
                    //本次按下距离上次的抬起小于1s时，取消Timer
                    timer.cancel();
                }
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE://手指滑动
                if (Math.abs(startY - event.getY()) > 10) {
                    if (isShowFloatImage) {
                        hideFloatImage(moveDistance);
                    }
                }
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP://手指抬起
                if (!isShowFloatImage) {
                    //抬起手指1s后再显示悬浮按钮
                    //开始1s倒计时
                    upTime = System.currentTimeMillis();
                    timer = new Timer();
                    timer.schedule(new FloatTask(), 1000);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private class FloatTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showFloatImage(moveDistance);
                }
            });
        }
    }

    private void hideFloatImage(int distance) {
        isShowFloatImage = false;

        //位移动画
        TranslateAnimation ta = new TranslateAnimation(0, distance, 0, 0);
        ta.setDuration(300);

        //渐变动画
        AlphaAnimation al = new AlphaAnimation(1f, 0.5f);
        al.setDuration(300);

        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        mIvCart.startAnimation(set);
    }

    private void showFloatImage(int distance) {
        isShowFloatImage = true;

        //位移动画
        TranslateAnimation ta = new TranslateAnimation(distance, 0, 0, 0);
        ta.setDuration(300);

        //渐变动画
        AlphaAnimation al = new AlphaAnimation(0.5f, 1f);
        al.setDuration(300);

        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        mIvCart.startAnimation(set);
    }

}
