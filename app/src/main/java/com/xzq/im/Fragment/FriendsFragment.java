package com.xzq.im.Fragment;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.ConnectionService;
import android.telecom.RemoteConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.xzq.im.Activity.ChatActivity;
import com.xzq.im.Adapter.ContactAdapter;
import com.xzq.im.R;
import com.xzq.im.Service.XmppService;
import com.xzq.im.bean.MsgList;
import com.xzq.im.bean.Users;
import com.xzq.im.bean.UsersInfomation;
import com.xzq.im.db.MessageHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lenovo on 2018/1/15.
 */

public class FriendsFragment extends Fragment {
    @BindView(R.id.elv_groups)
    ExpandableListView elvGroups;
    Unbinder unbinder;
    private XmppService ser;
    private List<Users> contact;
    private UsersInfomation user;
    private AbstractXMPPConnection conn1;
    XmppService.MyBinder binder;
    private ServiceConnection connt = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            binder = (XmppService.MyBinder) iBinder;
            ser=binder.getService();
            contact=ser.getContact();
            elvGroups.setGroupIndicator(null);
            ContactAdapter adapter = new ContactAdapter(contact, getActivity());
            conn1=ser.getAbstractXMPPConnection();
            //if(conn1.isConnected())
               // Toast.makeText(getActivity().getApplicationContext(), "哈哈哈哈哈"+contact.size(), Toast.LENGTH_LONG).show();
            elvGroups.setAdapter(adapter);
            user = ser.GetUser();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        getConnection();  //连接服务
       // ReConnection();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        elvGroups.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String friendName = contact.get(groupPosition).getDetails().get(childPosition).getUserIp();
                MsgList msgList = new MessageHelper(getActivity()).checkMsgList(user.getUser_id(), friendName);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("msg_list_id", msgList.getMsg_list_id());
                intent.putExtra("to_name", msgList.getTo_name());
                startActivity(intent);
                return false;
            }
        });
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
    public void onDestroyView() {
        //ser.unbindService(connt);
        super.onDestroyView();
        unbinder.unbind();
    }
}
