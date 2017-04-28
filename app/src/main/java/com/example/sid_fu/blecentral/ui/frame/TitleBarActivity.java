package com.example.sid_fu.blecentral.ui.frame;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.widget.TitleBarLayout;


/**
 * TitleBar Base Activity
 * * Created by jiangp on 16/3/15.
 */
public abstract class TitleBarActivity extends Activity {

    private LinearLayout mContainerView;
    private TitleBarLayout mTitleBarLayout;
    public abstract void setRootView();
    public abstract void setWidget();
    public abstract void setData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRootView();
        setWidget();
        setData();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());

        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        mContainerView.addView(contentView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private View getContainer() {
        mContainerView = new LinearLayout(this);
        mContainerView.setOrientation(LinearLayout.VERTICAL);
        mTitleBarLayout = new TitleBarLayout(this);
        setOnPageFinishListener();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getTitleBarHeight());
        mContainerView.addView(mTitleBarLayout, params);
        return mContainerView;
    }

    private void setOnPageFinishListener() {
        if (mTitleBarLayout == null) {
            return;
        }
        mTitleBarLayout.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void removeTitleBarLayout() {
        if (null != mTitleBarLayout) {
            mTitleBarLayout.setVisibility(View.GONE);
            mContainerView.removeView(mTitleBarLayout);
        }
    }

    public void addTitleBarLayout() {
        if (null != mTitleBarLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mContainerView.addView(mTitleBarLayout, 0, params);
        }
    }

    public TitleBarLayout getTitleBarLayout() {
        return mTitleBarLayout;
    }


    public void setTitleBarStyle(int style) {
        if (null != mTitleBarLayout) {
            mTitleBarLayout.setStyle(style);
        }
    }

    public void setTitleBarTxt(String titleTxt) {
        mTitleBarLayout.setTitleTxtString(titleTxt);
    }

    public void setTitleBarTxt(int resId) {
        mTitleBarLayout.setTitleTxtString(getResources().getString(resId));
    }


    public void setOnTitleBarClickListener(View.OnClickListener listener) {
        mTitleBarLayout.setOnBothSideClickListener(listener);
    }

    public void setOnTitleBarLeftClickListener(View.OnClickListener listener) {
        mTitleBarLayout.setOnLeftClickListener(listener);
    }

    public void setOnTitleBarRightClickListener(View.OnClickListener listener) {
        mTitleBarLayout.setOnRightClickListener(listener);
    }


    private int getTitleBarHeight() {
        return (int) getResources().getDimension(R.dimen.title_bar_height);
    }

    /**
     * show to @param(cls)，but can't finish activity
     */
    public void showActivity(Activity aty, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }
}