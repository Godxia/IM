package com.xzq.im.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xzq.im.Fragment.ChatListFragment;
import com.xzq.im.Fragment.FriendsFragment;
import com.xzq.im.Fragment.MyFragment;
import com.xzq.im.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_top)
    TextView tvTop;
    @BindView(R.id.ly_top_bar)
    RelativeLayout lyTopBar;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_friends)
    TextView tvFriends;
    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.tv_mysettings)
    TextView tvMysettings;
    @BindView(R.id.ly_tab_bar)
    LinearLayout lyTabBar;
    @BindView(R.id.div_tab_bar)
    View divTabBar;
    @BindView(R.id.ly_content)
    FrameLayout lyContent;
    private MyFragment fg3,fg4;
    private ChatListFragment fg1;
    private FriendsFragment fg2;
    private FragmentManager fManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fManager=getFragmentManager();
        tvFriends.performClick();
    }
    @OnClick({R.id.tv_message, R.id.tv_friends,R.id.tv_more,R.id.tv_mysettings})
    public void onClick(View view){
        FragmentTransaction fTransaction =  fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (view.getId()){
            case R.id.tv_message:
                setSelected();
                tvMessage.setSelected(true);
                if(fg1 == null){
                    fg1 = new ChatListFragment();
                    fTransaction.add(R.id.ly_content,fg1);
                }else{
                    fTransaction.show(fg1);
                }
                break;
            case R.id.tv_friends:
                setSelected();
                tvFriends.setSelected(true);
                if(fg2 == null){
                    fg2 = new FriendsFragment();
                    fTransaction.add(R.id.ly_content,fg2);
                }else{
                    fTransaction.show(fg2);
                }
                break;
            case R.id.tv_more:
                setSelected();
                tvMore.setSelected(true);
                if(fg3 == null){
                    fg3 = new MyFragment();
                    fTransaction.add(R.id.ly_content,fg3);
                }else{
                    fTransaction.show(fg3);
                }
                break;
            case R.id.tv_mysettings:
                setSelected();
                tvMysettings.setSelected(true);
                if(fg4 == null){
                    fg4 = new MyFragment();
                    fTransaction.add(R.id.ly_content,fg4);
                }else{
                    fTransaction.show(fg4);
                }
                break;
        }
        fTransaction.commit();
    }
    //重置所有文本的选中状态
    private void setSelected(){
        tvMessage.setSelected(false);
        tvFriends.setSelected(false);
        tvMore.setSelected(false);
        tvMysettings.setSelected(false);
    }
    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg1 != null)fragmentTransaction.hide(fg1);
        if(fg2 != null)fragmentTransaction.hide(fg2);
        if(fg3 != null)fragmentTransaction.hide(fg3);
        if(fg4 != null)fragmentTransaction.hide(fg4);
    }
}
