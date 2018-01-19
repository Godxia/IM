package com.xzq.im.Fragment;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.xzq.im.Activity.ChatActivity;
import com.xzq.im.Adapter.ChatListAdapter;
import com.xzq.im.R;
import com.xzq.im.Service.XmppService;
import com.xzq.im.bean.MsgList;
import com.xzq.im.bean.UsersInfomation;
import com.xzq.im.db.MessageHelper;
import com.xzq.im.rxbus.RxBus;

import org.jivesoftware.smack.packet.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lenovo on 2018/1/18.
 */


public class ChatListFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.lv_msglist)
    ListView lvMsglist;
    Unbinder unbinder;
    XmppService.MyBinder binder;
    private Subscription subscription;
    private XmppService ser;
    private List<MsgList> msgAllList;
    private ChatListAdapter adapter;
    private UsersInfomation user;
    private ServiceConnection connt = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (XmppService.MyBinder) service;
            ser=binder.getService();
            //ser.Connect();
            user=ser.GetUser();
            getList(user.getUser_id());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        unbinder = ButterKnife.bind(this, view);
        getConnection();
        newMsg();
        return view;
    }
    /**
     * 绑定连接服务
     */
    private void getConnection() {
        Intent intent = new Intent(getActivity(),XmppService.class);
        intent.setAction("XmppService");
        getActivity().bindService(intent,connt, Service.BIND_AUTO_CREATE);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("msg_list_id", msgAllList.get(position).getMsg_list_id());
        intent.putExtra("to_name", msgAllList.get(position).getTo_name());
        startActivity(intent);
    }
    /**
     * 获取消息列表
     *
     * @param userId
     */
    public void getList(int userId) {
        MessageHelper myHelper = new MessageHelper(getActivity());
        msgAllList = myHelper.getMsgAllList(userId);
        adapter = new ChatListAdapter(msgAllList, getActivity());
        lvMsglist.setAdapter(adapter);
        lvMsglist.setOnItemClickListener(this);
    }

    /**
     * 观察新消息
     */
    public void newMsg() {
        Toast.makeText(getActivity(), "执行了", Toast.LENGTH_SHORT).show();
        subscription = RxBus.getInstance().toObserverable(Message.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {
                        getList(user.getUser_id());
                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
