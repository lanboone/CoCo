package com.mymusic.cocomusic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.mymusic.cocomusic.util.Constent;

public class CoCoMusicApp extends Application{

	
	public  SharedPreferences sp;
	public  DbUtils dbUtils;
	public static  Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences(Constent.SP_NAME, Context.MODE_PRIVATE);
		dbUtils = DbUtils.create(getApplicationContext(),Constent.DB_NAME);
		context = getApplicationContext();//拿到当前程序的上下文
	}
}
