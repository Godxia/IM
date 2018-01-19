package com.xzq.im.Activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xzq.im.Adapter.ChatAdapter;
import com.xzq.im.R;
import com.xzq.im.Service.XmppService;
import com.xzq.im.bean.Msg;
import com.xzq.im.bean.UsersInfomation;
import com.xzq.im.db.MessageHelper;
import com.xzq.im.rxbus.RxBus;
import com.xzq.im.ui.EmoticonsEditText;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lenovo on 2018/1/16.
 */

public class ChatActivity extends BaseActivity {
    @BindView(R.id.tool_bar_title)
    TextView toolBarTitle;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.chat_view)
    RecyclerView chatView;
    @BindView(R.id.et_content)
    EmoticonsEditText etContent;
    @BindView(R.id.bt_send)
    Button btSend;
    @BindView(R.id.emo)
    ImageButton emo;
    @BindView(R.id.recycler_emo)
    RecyclerView recyclerEmo;
    XmppService.MyBinder binder;
    private XmppService ser;
    private ChatAdapter adapter;
    private int msg_list_id;
    private String to_name;
    private MessageHelper myhelper;
    private List<Msg> msgList = new ArrayList<>();
    private Subscription subscription;
    static AbstractXMPPConnection conn1;
    private UsersInfomation user;
    private ChatManager manager;
    private ServiceConnection connt = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (XmppService.MyBinder) service;
            ser = binder.getService();
            ser.getContact();
            user=ser.GetUser();
            conn1 = ser.getAbstractXMPPConnection();
            while(true){
                if(conn1!=null)break;
            }
            manager = ChatManager.getInstanceFor(conn1);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        msg_list_id = getIntent().getIntExtra("msg_list_id", -1);
        to_name = getIntent().getStringExtra("to_name");
       // initToolBar(true, to_name);
        Toast.makeText(getApplicationContext(),"to name是"+ to_name, Toast.LENGTH_LONG).show();
        getConnection();
        myhelper = new MessageHelper(this);
        getMsg();
        newMsg();
        chatView.scrollToPosition(msgList.size() - 1);//让界面到最新聊天
        // initEmo();
    }

    /**
     * 绑定连接服务
     */
    private void getConnection() {
        Intent intent = new Intent(this, XmppService.class);
        intent.setAction("XmppService");
        bindService(intent, connt, Service.BIND_AUTO_CREATE);
    }

    @OnClick({R.id.bt_send, R.id.emo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send:
                String body = etContent.getText().toString();
                String receiver = to_name ;
                try {
                    //ChatManager manager = ChatManager.getInstanceFor(conn1);
                    EntityBareJid jid = JidCreate.entityBareFrom(receiver);//获取接受者jid
                    Chat chat = manager.chatWith(jid);
                    Message message = new Message(jid, Message.Type.chat);
                   // message.setTo(jid);//
                    message.setBody(body);
                    chat.send(message);//发送信息
                    msgList.add(new Msg(user.getUser_name(), body, System.currentTimeMillis() + "", "text", 1));
                    myhelper.insertOneMsg(user.getUser_id(), to_name, body, System.currentTimeMillis() + "", user.getUser_name(), 1);//保存信息
                    adapter.notifyDataSetChanged();//显示发出的信息。
                    etContent.setText("");
                    chatView.scrollToPosition(msgList.size() - 1);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.emo:
                break;
        }
    }


    /**
     * 从数据库中获取聊天记录
     */
    public void getMsg() {
        msgList.clear();
        msgList.addAll(myhelper.getAllMsg(msg_list_id, -1));
        if (adapter == null) {
            chatView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ChatAdapter(msgList, this);
            chatView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "更新了", Toast.LENGTH_SHORT).show();
        }
        chatView.scrollToPosition(msgList.size()-1);
    }

    /**
     * 接收新消息
     */
    public void newMsg() {
        Toast.makeText(this, "执行了", Toast.LENGTH_SHORT).show();
        subscription = RxBus.getInstance().toObserverable(Message.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {
                            getMsg();
                    }
                });
    }
    @Override
    protected void onDestroy() {
        unbindService(connt);
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}