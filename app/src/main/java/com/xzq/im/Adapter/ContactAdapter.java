package com.xzq.im.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xzq.im.R;
import com.xzq.im.bean.Users;

import java.security.acl.Group;
import java.util.List;

/**
 * Created by lenovo on 2018/1/15.
 */

public class ContactAdapter extends BaseExpandableListAdapter {
    private List<Users> list;
    private Context context;

    public ContactAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getDetails().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getDetails().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fragment_friends, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_friends);
        if (isExpanded) {
            imageView.setImageResource(R.drawable.open);
        }else{
            imageView.setImageResource(R.drawable.close);
        }
        tv.setText(list.get(groupPosition).getGroupName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView view = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        view.setText(list.get(groupPosition).getDetails().get(childPosition).getPickName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
