package com.xzq.im.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.xzq.im.Activity.ChatActivity;
import com.xzq.im.bean.Msg;
import com.xzq.im.bean.MsgList;
import com.xzq.im.bean.Users;
import com.xzq.im.bean.UsersInfomation;
import com.xzq.im.db.MessageHelper;
import com.xzq.im.db.MyHelper;
import com.xzq.im.rxbus.RxBus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by lenovo on 2018/1/13.
 */

public class XmppService extends Service {
    @Nullable
    static AbstractXMPPConnection conn;
    private UsersInfomation user;//用户信息
    private MessageHelper myHelper;
    private int i=0;
    private MyBinder binder = new MyBinder();
    public class MyBinder extends Binder{

            public XmppService getService() {
            return XmppService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void Connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String server="192.168.0.102";
                    InetAddress addr = InetAddress.getByName("192.168.0.102");
                    HostnameVerifier verifier = new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return false;
                        }
                    };
                    DomainBareJid serviceName = JidCreate.domainBareFrom(server);
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setHost(server) //it will be resolved by setHostAddress method
                            // .setUsernameAndPassword("xzq","123")
                            .setPort(5222)
                            .setServiceName(serviceName)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .setXmppDomain(serviceName)
                            .setHostnameVerifier(verifier)
                            .setHostAddress(addr)
                            .setDebuggerEnabled(true)
                            .build();
                    conn = new XMPPTCPConnection(config);

                    conn.connect();

                    if(conn.isConnected()){
                        Log.d("XMPP","Connected");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /****
     * 得到连接
     * @return  AbstractXMPPConnection
     */
    public AbstractXMPPConnection getAbstractXMPPConnection(){
        return conn;
    }
    /***
     * 插入用户
     */
    public void SetUser(String username,String password){
        myHelper = new MessageHelper(this);
        user = myHelper.SetUser(username + "@192.168.0.102", password);//插入数据库
    }
    /**
     * 获取用户信息
     *
     * @return
     */
    public UsersInfomation GetUser() {
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }
    /**
     * 获得所有联系人
     */
    public List<Users> getContact() {
        List<Users> list = new ArrayList<>();
        if (conn!= null) {
            Roster roster = Roster.getInstanceFor(conn);//根据当前连接获取roster对象
            Collection<RosterGroup> groups = roster.getGroups();//得到组
            List<Users.UsersDetails> detail = new ArrayList<>();
            for (RosterGroup group : groups) {
                Users userBean = new Users();
                userBean.setGroupName(group.getName());
                List<RosterEntry> entries = group.getEntries();
                for (RosterEntry entry : entries) {
                    Users.UsersDetails user = new Users.UsersDetails();
                    user.setUserIp(entry.getUser());
                    user.setPickName(entry.getName());
                    user.setType(entry.getType());
                   // user.setStatus(entry.getStatus());
                    detail.add(user);
                    userBean.setDetails(detail);
                }
                list.add(userBean);
            }
        }
        return list;
    }
    public void initListener() {
        if(conn==null)
            conn=getAbstractXMPPConnection();
        ChatManager chatManager = ChatManager.getInstanceFor(conn);
        InChatMessageListener inListener = new InChatMessageListener();
        chatManager.addIncomingListener(inListener);
    }

    public class InChatMessageListener implements IncomingChatMessageListener {

        @Override
        public void newIncomingMessage(final EntityBareJid from, final Message message, Chat chat) {
            //Toast.makeText(getApplicationContext(),from.toString()+message.getBody(), Toast.LENGTH_LONG).show();
           // msgList.add(new Msg(from.toString(), message.getBody(), System.currentTimeMillis() + "", "text", 2));

            myHelper.insertOneMsg(user.getUser_id(), from.toString(), message.getBody(), System.currentTimeMillis() + "", message.getTo().toString(), 2);
           // adapter.notifyDataSetChanged();//显示发出的信息。
            RxBus.getInstance().post(message);
    }
    }

}
