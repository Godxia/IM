package com.xzq.im.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xzq.im.R;
import com.xzq.im.Service.XmppService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jxmpp.jid.parts.Localpart;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by lenovo on 2018/1/11.
 */

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.et_regusername)
    EditText etRegusername;
    @BindView(R.id.et_regpassword)
    EditText etRegpassword;
    @BindView(R.id.et_repeatregpassword)
    EditText etRepeatregpassword;
    @BindView(R.id.bt_register)
    Button btRegister;
    @BindView(R.id.cv_add)
    CardView cvAdd;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    static AbstractXMPPConnection conn1;
    XmppService.MyBinder binder;
    HashMap<String,String> attr=new HashMap<String, String>();

    private ServiceConnection connt = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (XmppService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getConnection();//连接到服务器
        //attr.put("email","email");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }
    @OnClick(R.id.bt_register)
    public void onClick(View view){
        conn1=binder.getAbstractXMPPConnection();
        try { //先登录才能注册
            if(!conn1.isConnected()){//重连
                unbindService(connt);
                getConnection();
                conn1=binder.getAbstractXMPPConnection();
            }
            if(conn1.isConnected()){
                // Log into the server
                conn1.login("admin","htdddwg1234");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String username = etRegusername.getText().toString();
        final String password = etRegpassword.getText().toString();
        //final String email = etRepeatregpassword.getText().toString();
        if(register(username,password,attr)){
            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
          //  unbindService(connt);
            conn1.disconnect();
        }
        else{
            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_LONG).show();
        }
    }


    public boolean register(String username, String password, HashMap<String,String> attr) {
        AccountManager manager = AccountManager.getInstance(conn1);
        manager.sensitiveOperationOverInsecureConnection(true);
        try {
                manager.createAccount(Localpart.from(username), password,attr);
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getConnection() {
        Intent intent = new Intent(this,XmppService.class);
        intent.setAction("XmppService");
        bindService(intent,connt, Service.BIND_AUTO_CREATE);
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
