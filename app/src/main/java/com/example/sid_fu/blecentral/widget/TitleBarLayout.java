package com.example.sid_fu.blecentral.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sid_fu.blecentral.R;


/**
 * TitleBar layout
 * Created by jiangp on 16/3/15.
 */
public class TitleBarLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "title bar";

    private View mItemLeft;
    private ImageView mIvLeft;
    private TextView mTvLeft;
    private TextView mTvTitle;
    private ImageView mIvRight;

    public TitleBarLayout(Context context) {
        this(context, null);
    }

    public TitleBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
//        setBackgroundColor(getResources().getColor(R.color.theme_red));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.title_bar_layout, null);
        this.addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mItemLeft = view.findViewById(R.id.item_left);
        mIvLeft = (ImageView) view.findViewById(R.id.iv_left);
        mTvLeft = (TextView) view.findViewById(R.id.tv_left);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mIvRight = (ImageView) view.findViewById(R.id.iv_right);


        mItemLeft.setOnClickListener(this);
        mTvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
//        setStyle(0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    //-------------------attribute setting start-----------------------------
    //title
    public void setTitleTxtString(String txtString) {
        mTvTitle.setText(txtString);
    }

    public void setTitleTxtSize(int textSize) {
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public void setTitleTxtColor(int color) {
        mTvTitle.setTextColor(color);
    }

    //-------------------click event start-----------------------------
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.item_left:
                onTitleLeftClick(v);
                break;
            case R.id.iv_right:
                onTitleRightClick(v);
                break;
            case R.id.tv_title:

                break;
            case R.id.tv_left:
                onTitleLeftClick(v);
                break;
        }
    }

    private void onTitleLeftClick(View view) {
        if (null != mOnLeftClickListener) {
            mOnLeftClickListener.onClick(view);
        }
        if (null != mOnBothSideClickListener) {
            mOnBothSideClickListener.onClick(view);
        }
    }

    private void onTitleRightClick(View view) {
        if (null != mOnRightClickListener) {
            mOnRightClickListener.onClick(view);
        }
        if (null != mOnBothSideClickListener) {
            mOnBothSideClickListener.onClick(view);
        }
    }

    private OnClickListener mOnLeftClickListener;
    private OnClickListener mOnRightClickListener;
    private OnClickListener mOnBothSideClickListener;


    public void setOnLeftClickListener(OnClickListener listener) {
        mOnLeftClickListener = listener;
    }

    public void setOnRightClickListener(OnClickListener listener) {
        mOnRightClickListener = listener;
    }

    public void setOnBothSideClickListener(OnClickListener listener) {
        mOnBothSideClickListener = listener;
    }
    //-------------------click event end-----------------------------

    private int dip2px(int dipValue) {
        int pxValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                getResources().getDisplayMetrics());
        return pxValue;
    }

    private int sp2px(int spValue) {
        int pxValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                getResources().getDisplayMetrics());
        return pxValue;
    }

    /**
     * 设置显示样式
     *
     * @param style 0:灰色背景,只有一个 "返回"按钮
     *              1:红色背景,右边按钮可见
     */
    public void setStyle(int style) {
        switch (style) {
            case 0:
                mTvTitle.setText("胎压监测-蓝牙版");
                mTvTitle.setTextColor(getResources().getColor(R.color.white));
                mIvRight.setVisibility(View.VISIBLE);
                mIvRight.setImageResource(R.drawable.return_btn_gray);
                break;
            case 1:
                mTvTitle.setText("设置");
                mTvTitle.setTextColor(getResources().getColor(R.color.white));
                mIvLeft.setVisibility(View.VISIBLE);
                mIvLeft.setImageResource(R.drawable.return_btn_white);
                break;
            case 2:
                //mTvTitle.setText("胎压监测-蓝牙版");
                mTvTitle.setTextColor(getResources().getColor(R.color.white));
                mIvRight.setVisibility(View.VISIBLE);
                mIvRight.setImageResource(R.drawable.return_btn_gray);
                break;
        }
    }
}