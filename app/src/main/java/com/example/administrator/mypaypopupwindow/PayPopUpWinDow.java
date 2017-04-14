package com.example.administrator.mypaypopupwindow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class PayPopUpWinDow implements View.OnClickListener {
    public static final int Alipay = 0;
    public static final int WXpay = 1;
    private PopupWindow popupWindow;
    private View rootView;
    private Button btnPay;
    private ImageView ivClose;
    private TextView tvPayHint;
    private Context context;
    private LinearLayout llPayChannel;
    private LayoutInflater layoutInflater;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private int choosePosition;
    private View attachView;
    private int showTimerSeconeds;
    private PayBtnListener payBtnListener;
    private TimeOutListener timeOutListener;
    public interface PayBtnListener {
        void onClick(View v, int payChannel);
    }
    public interface TimeOutListener{
        void timeOut();
    }

    public PayPopUpWinDow(Context context, boolean isShowerTimer) {
        initViews(context, isShowerTimer);
        initListener();
    }

    private void initListener() {
        ivClose.setOnClickListener(this);
        btnPay.setOnClickListener(this);
    }

    private void initViews(Context context, boolean isShowerTimer) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.popupw_pay, null);
        popupWindow = new PopupWindow(context);
        popupWindow.setAnimationStyle(R.style.popupfrombottom);
        popupWindow.setContentView(rootView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        if (isShowerTimer) {
            popupWindow.setHeight(new DisPlayUtil().dp2px(345));
        } else {
            popupWindow.setHeight(new DisPlayUtil().dp2px(315));
        }
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        llPayChannel = (LinearLayout) rootView.findViewById(R.id.ll_pay_channel);
        btnPay = (Button) rootView.findViewById(R.id.btn_pay);
        ivClose = (ImageView) rootView.findViewById(R.id.iv_close);
        tvPayHint = (TextView) rootView.findViewById(R.id.tv_pay_hint);

        setPayContentViews();
    }


    public void show(View view) {
        if (showTimerSeconeds > 0) {
            showTimer(showTimerSeconeds);
        }
        attachView = view;
        view.setBackgroundColor(0x40000000);//设置背景色变暗

        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void dissmiss() {
        attachView.setBackgroundColor(0xffffff);
        popupWindow.dismiss();
    }


    //设置支付方式
    public void setupChannelView( Payitem... payitems) {
        llPayChannel.removeAllViews();
        for (Payitem v : payitems) {
            llPayChannel.addView(inflatePayItem(v.icon, v.payName, v.payHint, v.position));
        }
    }

    private View inflatePayItem(int icon, String title, String hint, final int position) {
        View view = layoutInflater.inflate(R.layout.payway_item, null);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_check);
        ImageView iv_imageView = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_payname);
        TextView tv_hint = (TextView) view.findViewById(R.id.tv_tips);
        iv_imageView.setImageResource(icon);
        tv_title.setText(title);
        tv_hint.setText(hint);
        if (position == choosePosition) {
            checkBox.setChecked(true);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox c : checkBoxes) {
                    c.setChecked(false);
                }
                choosePosition = position;
                checkBoxes.get(position).setChecked(true);
            }
        });
        checkBoxes.add(checkBox);
        return view;

    }

    private void setPayContentViews() {
        Payitem[] payitems = new Payitem[2];
        payitems[0] = new Payitem(R.drawable.weixin2x, "支付宝支付", "推荐有支付宝账号的用户使用", 0);
        payitems[1] = new Payitem(R.drawable.weixin2x, "微信支付", "balabalalbala...", 1);
        setupChannelView( payitems);
    }

    //显示计时器
    public void showTimer(int seconeds) {
        timer(seconeds);
        startAnimator(seconeds);
    }
    public void dismissTimer(){
        tvPayHint.setVisibility(View.GONE);
    }

    private void timer(int seconed) {
        String minute = handleSeconed(seconed);
        //String msg = "您还有 " + minute + "去支付";
        //String msg=String.format(getResources().getString(R.string.haiyou),minute);
        String msg = "您还有 " + minute + " 去支付,超时自动取消";
        //Log.e("xxx", msg.length() + "");
        SpannableString spannableString = new SpannableString(msg);
        spannableString.setSpan(new AbsoluteSizeSpan(new DisPlayUtil().sp2px(13)), 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(styleSpan, 4, 4 + minute.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(new DisPlayUtil().sp2px(15)), 4,
                4 + minute.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPayHint.setText(spannableString);
        tvPayHint.setVisibility(View.VISIBLE);
    }

    private void startAnimator(int seconed) {
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(seconed, 0);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(seconed * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = (int) animation.getAnimatedValue();
                showTimerSeconeds = i;
                timer(i);

            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (timeOutListener!=null){
                    timeOutListener.timeOut();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        //valueAnimator.cancel();
    }

    private String handleSeconed(int seconed) {
        int minute = seconed / 60;
        int seconeds = seconed % 60;
        String s = minute + "分" + seconeds + "秒";
        return s;
    }

    public void setShowTimerSeconeds(int seconeds) {
        this.showTimerSeconeds = seconeds;
    }

    //暴露接口
    public void setPayBtnListener(PayBtnListener l) {
        this.payBtnListener = l;
    }

    public void setTimeOutListener(TimeOutListener l){
        this.timeOutListener=l;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dissmiss();
                break;
            case R.id.btn_pay:
                if (payBtnListener != null) {
                    payBtnListener.onClick(v, choosePosition);
                }
                break;
        }
    }


    static class Payitem {
        public int position;
        public int icon;
        public String payName;
        public String payHint;

        public Payitem(int icon, String payName, String PayHint, int position) {
            this.icon = icon;
            this.payName = payName;
            this.payHint = PayHint;
            this.position = position;
        }
    }

    public class DisPlayUtil {
        public int dp2px(int dp) {
            return (int) (context.getResources().getDisplayMetrics().density * dp);
        }

        public int sp2px(int sp) {
            return (int) (context.getResources().getDisplayMetrics().scaledDensity * sp);
        }

    }
}
