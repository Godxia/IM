package com.xzq.im.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.net.InetAddress;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by lenovo on 2018/1/13.
 */

public class XmppService extends Service {
    @Nullable
    static AbstractXMPPConnection conn;
    private MyBinder binder = new MyBinder();
    public class MyBinder extends Binder{
            public AbstractXMPPConnection getAbstractXMPPConnection(){
                return conn;
            }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
                   // DomainBareJid serviceName2 = JidCreate.domainBareFrom("localhost");
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

}
