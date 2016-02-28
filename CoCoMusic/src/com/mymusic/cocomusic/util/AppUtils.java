package com.mymusic.cocomusic.util;

import com.mymusic.cocomusic.CoCoMusicApp;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AppUtils {
	
	//�������뷨
	public static void hideInputMethod(View view){
		InputMethodManager imm = (InputMethodManager) CoCoMusicApp.context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
		}
	}

}
