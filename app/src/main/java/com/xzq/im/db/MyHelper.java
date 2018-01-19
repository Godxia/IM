package com.xzq.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2018/1/15.
 */

public class MyHelper extends SQLiteOpenHelper{
    public static String TABLE = "chatMessage";//聊天信息表

    public MyHelper(Context context) {
        super(context, "smack.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user" +
                "(user_id  INTEGER primary key autoincrement," +
                "user_name varchar," +
                "user_psd varchar," +
                "user_head_img varchar)");
        db.execSQL("create table msg_list" +
                "(msg_list_id  INTEGER primary key autoincrement," +
                "user_id INTEGER," +
                "to_name varchar," +
                "last_msg varchar," +
                "last_msg_time varchar," +
                "msg_list_type)");
        db.execSQL("create table msg" +
                "(msg_id INTEGER primary key autoincrement," +
                "msg_list_id  INTEGER," +
                "from_name varchar," +
                "msg_content varchar," +
                "msg_time varchar," +
                "msg_type varchar ," +
                "from_type INTEGER)");

//        db.execSQL("create table msg_list(msg_list_id  INTEGER primary key autoincrement,user_id INTEGER,to_name varchar,last_image varchar,last_img_time varchar,msg_list_type, foreign key (user_id) references  user(user_id) on delete cascade on update cascade )");
//        db.execSQL("create table msg(msg_id INTEGER primary key autoincrement,msg_list_id  INTEGER,from_name varchar,msg_content varchar,msg_time varchar,msg_type varchar, foreign key (msg_list_id) references  msg_list(msg_list_id) on delete cascade on update cascade )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

