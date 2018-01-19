package com.xzq.im.Activity;

import android.app.ActivityOptions;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xzq.im.R;
import com.xzq.im.Service.XmppService;
import com.xzq.im.bean.UsersInfomation;
import com.xzq.im.db.MessageHelper;
import com.xzq.im.db.MyHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lenovo on 2018/1/11.
 */

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.cv)
    CardView cv;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    static AbstractXMPPConnection  conn1;
    XmppService.MyBinder binder;
    private XmppService ser;
    private ServiceConnection connt = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (XmppService.MyBinder) service;
            ser=binder.getService();
            ser.Connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Toast.makeText(getApplicationContext(), "哈哈哈哈哈", Toast.LENGTH_LONG).show();
        Log.d("哈哈","shabi");
        getConnection();//连接到服务器
    }

    /**
     * 绑定连接服务
     */
    private void getConnection() {
        Intent intent = new Intent(this,XmppService.class);
        intent.setAction("XmppService");
        bindService(intent,connt, Service.BIND_AUTO_CREATE);
    }

    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "账号或密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (login(username, password)) {
                    Explode explode = new Explode();
                    explode.setDuration(500);
                    getWindow().setExitTransition(explode);
                    getWindow().setEnterTransition(explode);
                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                    Intent i2 = new Intent(this, MainActivity.class);
                    startActivity(i2, oc2.toBundle());
                    break;
                }
        }
    }

    public boolean login(String username, String password) {

        conn1=ser.getAbstractXMPPConnection();
        try {
            if(!conn1.isConnected()){//重连
                ser.Connect();
            }
            if(conn1.isConnected()){
                // Log into the server
                conn1.login(username,password);
                ser.SetUser(username, password);//将用户插入数据库
                ser.initListener();
                Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "服务器未连接\n请重试", Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onDestroy(){
       // unbindService(connt);
        super.onDestroy();
    }
}