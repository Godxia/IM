package com.xzq.im.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xzq.im.R;
import com.xzq.im.bean.MsgList;

import java.util.List;

/**
 * Created by lenovo on 2018/1/18.
 */

public class ChatListAdapter extends BaseAdapter {
    private List<MsgList> list;
    private Context context;

    public ChatListAdapter(List<MsgList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fragment_chatlist, parent, false);
        }
        //View view = LayoutInflater.from(context).inflate(R.layout.item_fragment_chatlist, null);
        TextView name = (TextView) convertView.findViewById(R.id.tv_friend_name);
        TextView msg = (TextView) convertView.findViewById(R.id.tv_friend_msg);
        String from = list.get(position).getTo_name().split("@")[0];
        name.setText(from);
        msg.setText(list.get(position).getLast_msg());
        return convertView;
    }
}
