package com.mymusic.cocomusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class FullscreenActivity extends Activity {
	private static final int START_ACTIVITY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fullscreen);
        Log.d("gogo", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI+"................");
        
        //»•±ÍÃ‚
        Intent intent = new Intent(this,PlayService.class);
       
        
        startService(intent);
        handler.sendEmptyMessageDelayed(START_ACTIVITY, 3000);
    }
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		super.handleMessage(msg);
    		switch (msg.what) {
			case START_ACTIVITY:
				startActivity(new Intent(FullscreenActivity.this,com.mymusic.cocomusic.MainActivity.class));
				finish();
				break;

			default:
				break;
			}
    	};
    };
}
