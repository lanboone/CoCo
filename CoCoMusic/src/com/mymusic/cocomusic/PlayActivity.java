package com.mymusic.cocomusic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mymusic.cocomusic.util.Constent;
import com.mymusic.cocomusic.util.MediaUtils;
import com.mymusic.cocomusic.vo.Mp3Info;
import douzi.android.view.DefaultLrcBuilder;
import douzi.android.view.ILrcBuilder;
import douzi.android.view.ILrcView;
import douzi.android.view.LrcRow;
import douzi.android.view.LrcView;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends BaseActivity implements OnClickListener,OnSeekBarChangeListener,OnPageChangeListener{

	private TextView textView_end_time,textView_title,textView_start_time;
	static ImageView imageView1_album;
	private ImageView imageButton1_play_mode;
	private ImageView imageButton2_play_pause;
	private ImageView imageButton3_previous;
	private ImageView imageButton1_next;
	private ImageView imageView_favorite;
	private SeekBar seekBar1;
//	private ArrayList<Mp3Info> mp3Infos;
	private static final int UPDATE_TIME = 0;//更新播放时间的标记
	private static final int UPDATE_LRC = 1;//更新歌词
	private CoCoMusicApp app;
	private ViewPager viewPager;
	private LrcView lrcView;
	private ArrayList<View> views = new ArrayList<View>();
	private Animation animation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_play);
		
//		mDbUtils = DbUtils.create(this, Constent.DB_NAME);
		textView_end_time = (TextView) findViewById(R.id.textView_end_time);
		textView_title = (TextView)findViewById(R.id.textView_title);
		textView_start_time = (TextView) findViewById(R.id.textView_start_time);
		imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
		
		imageButton1_play_mode = (ImageView) findViewById(R.id.imageButton1_play_mode);
		imageButton2_play_pause = (ImageView) findViewById(R.id.imageButton2_play_pause);
		imageButton3_previous = (ImageView) findViewById(R.id.imageButton3_previous);
		imageButton1_next = (ImageView) findViewById(R.id.imageButton1_next);
		imageView_favorite = (ImageView) findViewById(R.id.imageView_favorite);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		initViewPager();
		imageButton2_play_pause.setOnClickListener(this);
		imageButton1_play_mode.setOnClickListener(this);
		imageButton1_next.setOnClickListener(this);
		imageButton3_previous.setOnClickListener(this);
		seekBar1.setOnSeekBarChangeListener(this);
		imageView_favorite.setOnClickListener(this);
		app = (CoCoMusicApp) getApplication();

		
		
		myHandler = new MyHandler(this);
		
	}
	public Bitmap makeRoundCorner(Bitmap bitmap)
	  {
		Log.d("dodo", "lala");
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    int left = 0, top = 0, right = width, bottom = height;
	    float roundPx = height/2;
	    if (width > height) {
	      left = (width - height)/2;
	      top = 0;
	      right = left + height;
	      bottom = height;
	    } else if (height > width) {
	      left = 0;
	      top = (height - width)/2;
	      right = width;
	      bottom = top + width;
	      roundPx = width/2;
	    }
//	    ZLog.i(TAG, "ps:"+ left +", "+ top +", "+ right +", "+ bottom);

	    Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	    int color = 0xff424242;
	    Paint paint = new Paint();
	    Rect rect = new Rect(left, top, right, bottom);
	    RectF rectF = new RectF(rect);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
	  }
	private void initViewPager(){
		//动画初始化
				animation = AnimationUtils.loadAnimation(this, R.anim.tip);
				LinearInterpolator li = new LinearInterpolator();
			    animation.setInterpolator(li);
			    
			    
			    
			    
		View album_image_layout = getLayoutInflater().inflate(R.layout.album_image_layout, null);
		imageView1_album = (ImageView) album_image_layout.findViewById(R.id.imageView1_album);
		textView_title = (TextView) album_image_layout.findViewById(R.id.textView_title);
		views.add(album_image_layout);
		View lrc_layout = getLayoutInflater().inflate(R.layout.lrc_layout, null);
		lrcView = (LrcView) lrc_layout.findViewById(R.id.lrcView);
		lrcView.setListener(new ILrcView.LrcViewListener() {
			
			@Override
			public void onLrcSeeked(int newPosition, LrcRow row) {
				if(playService.isPlaying()){
					playService.seekTo((int) row.time);
				}
			}
		});
		lrcView.setLoadingTipText("正在加载歌词");
		lrcView.setBackgroundResource(R.drawable.jb_bg);
		lrcView.getBackground().setAlpha(150);
		views.add(lrc_layout);
		viewPager.setAdapter(new MyPagerAdapter());
//		viewPager.addOnPageChangeListener(this);
	}
	//加载歌词
	private void loadLRC(File lrcFile){
		StringBuffer buf = new StringBuffer(1024*10);
		char[] chars = new char[1024];
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile)));
			int len = -1;
			while((len = in.read(chars))!=-1){
				buf.append(chars,0,len);
			}
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ILrcBuilder builder = new DefaultLrcBuilder();
		List<LrcRow> rows = builder.getLrcRows(buf.toString());
		lrcView.setLrc(rows);
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		bindPlayService();
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		unbindPlayService();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private static MyHandler myHandler;
	static class MyHandler extends Handler{
		
		private PlayActivity playActivity;
		public MyHandler(PlayActivity playActivity){
			this.playActivity = playActivity;//可以使用软引用
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(playActivity !=null){
				switch (msg.what) {
				case UPDATE_TIME:
					playActivity.textView_start_time.setText(MediaUtils.formatTime(msg.arg1));
					break;
				}
			}
		}
	}
	@Override
	public void publish(int progress) {
		Message msg = myHandler.obtainMessage(UPDATE_TIME);
		msg.arg1 = progress;
		myHandler.sendMessage(msg);
		seekBar1.setProgress(progress);
		
		//-------------------------------------------
		
		
	}


	
	@Override
	public void change(int position) {

		  Mp3Info mp3Info = playService.mp3Infos.get(position);
		  textView_title.setText(mp3Info.getTitle());
		  Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
		  
//		  Bitmap bmp= ((BitmapDrawable)imageView1_album.getDrawable()).getBitmap();//将图片转化圆形
		  imageView1_album.setImageBitmap(makeRoundCorner(albumBitmap));
		  
//		  imageView1_album.setImageBitmap(albumBitmap);
		  textView_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
		  seekBar1.setProgress(0);
		  seekBar1.setMax((int) mp3Info.getDuration());
		  if(playService.isPlaying()){
				imageButton2_play_pause.setImageResource(R.mipmap.zanting);
				imageView1_album.startAnimation(animation);
			}else {
				imageButton2_play_pause.setImageResource(R.mipmap.bofang);
				imageView1_album.clearAnimation();
			}
		  switch (playService.getPlay_mode()) {
		       case PlayService.ORDER_PLAY:
		    	   imageButton1_play_mode.setImageResource(R.mipmap.order);
		    	   imageButton1_play_mode.setTag(PlayService.ORDER_PLAY);			
			       break;
		       case PlayService.RANDOM_PLAY:
		    	   imageButton1_play_mode.setImageResource(R.mipmap.random);
		    	   imageButton1_play_mode.setTag(PlayService.RANDOM_PLAY); 
		    	   break;
		       case PlayService.SINGLE_PLAY:
		    	   imageButton1_play_mode.setImageResource(R.mipmap.single);
		    	   imageButton1_play_mode.setTag(PlayService.SINGLE_PLAY);
		    	   break;
		      
		  }
		  //初始化收藏状态
		  try {
			Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoIdd", "=", mp3Info.getMp3InfoIdd()));
            Log.d("doooooo", mp3Info.getMp3InfoIdd()+"playActivity");
			if(likeMp3Info!=null){
				imageView_favorite.setImageResource(R.mipmap.xin_hong);
			}else{
				
				imageView_favorite.setImageResource(R.mipmap.xin_bai);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		  
		  
		  
		  
		  
		//歌词
		String songName = mp3Info.getTitle();
		String lrcPath = Environment.getExternalStorageDirectory() +Constent.DIR_LRC +"/"+songName+".lrc";
		File lrcFile = new File(lrcPath);
//		if(!lrcFile.exists()){
//			//下载
//			SearchMusicUtils.getInstance().setListener(new SearchMusicUtils.OnSearchResultListener() {
//				
//				@Override
//				public void onSearchResult(ArrayList<SearchResult> results) {
//                 	SearchResult searchResult = results.get(0);			
//                 	String url = Constent.BAIDU_URL + searchResult.getUrl();
//                 	DownloadUtils.getInstance().downloadLRC(url,searchResult.getMusicName(),myHandler);
//                 	
//				}
//			}).search(songName+" "+mp3Info.getArtist(),1);
//		}else{
			loadLRC(lrcFile);
//		}
	}
//	private long getId(Mp3Info mp3Info){
//		//初始收藏状态
//		long id = 0;
//		switch (playService.getChangePlayList()) {
//		case PlayService.MY_MUSIC_LIST:
//			id = mp3Info.getId();
//			
//			break;
//		case PlayService.LIKE_MUSIC_LIST:
//			id = mp3Info.getMp3InfoIdd();
//			break;
//			
//
//		default:
//			break;
//		}
//		return id;
//	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButton2_play_pause:{
			if(playService.isPlaying()){
				imageButton2_play_pause.setImageResource(R.mipmap.bofang);
				imageView1_album.clearAnimation();
				playService.pause();
			}else{
				imageView1_album.startAnimation(animation);
				
				if(playService.isPause()){
					imageButton2_play_pause.setImageResource(R.mipmap.zanting);
					playService.start();
				}else{
					playService.play(playService.getCurrentPosition());
				}
				
			}
			
			break;
		}
		case R.id.imageButton1_next:{
			playService.next();
			break;
		}
		case R.id.imageButton3_previous:{
			playService.prev();
			break;
		}
		case R.id.imageButton1_play_mode:{
			int mode = (Integer) imageButton1_play_mode.getTag();
			switch (mode) {
			case PlayService.ORDER_PLAY:
				imageButton1_play_mode.setImageResource(R.mipmap.random);
				imageButton1_play_mode.setTag(PlayService.RANDOM_PLAY);
				playService.setPlay_mode(PlayService.RANDOM_PLAY);
				 Toast.makeText(PlayActivity.this,getString(R.string.random), Toast.LENGTH_SHORT).show();
				break;
			case PlayService.RANDOM_PLAY:
				imageButton1_play_mode.setImageResource(R.mipmap.single);
				imageButton1_play_mode.setTag(PlayService.SINGLE_PLAY);
				playService.setPlay_mode(PlayService.SINGLE_PLAY);
				 Toast.makeText(PlayActivity.this,getString(R.string.single), Toast.LENGTH_SHORT).show();
				break;
			case PlayService.SINGLE_PLAY:
				imageButton1_play_mode.setImageResource(R.mipmap.order);
				imageButton1_play_mode.setTag(PlayService.ORDER_PLAY);
				playService.setPlay_mode(PlayService.ORDER_PLAY);
				 Toast.makeText(PlayActivity.this,getString(R.string.order), Toast.LENGTH_SHORT).show();
				break;	

			}
			break;
		}
		/*
		 * 为什么要加mp3InfoIdd字段?
		 * 因为多媒体数据库有一个表，id为自增长。所以要想在dbUtils表里拿到这首歌的id，不能直接在用id，因为存这首歌的id也是自增长的
		 * 比如id=171；，第一次存在dbUtils里只能是1，第二次为2，所以下次要拿时还是拿不到171；所以不能用自增长的id，用mp3InfoIdd
		 * 用mp3InfoIdd作为这首歌的一个条件跟着，只要条件符合就能拿到这首歌
		 * 
		 */
		case R.id.imageView_favorite:{
			Mp3Info mp3Info = playService.mp3Infos.get(playService.getCurrentPosition());
			
			Log.d("dodo", mp3Info+"啦啦");
			try {
				//xUtil保存会替换原ID，所以重新弄个id来保存，这里是Mp3InfoId
				Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoIdd","=",mp3Info.getMp3InfoIdd()));
				Log.d("dodo", likeMp3Info+"啊啊");
				Log.d("doooooo", "点击后mp3Info.getMp3InfoIdd()="+mp3Info.getMp3InfoIdd());
				if(likeMp3Info==null){
				    mp3Info.setMp3InfoIdd(mp3Info.getId());
				    Log.d("doooooo", "点击后mp3Info.getMp3InfoIdd()="+mp3Info.getMp3InfoIdd());
//				    mp3Info.setIsLike(1);
				    app.dbUtils.save(mp3Info);
					imageView_favorite.setImageResource(R.mipmap.xin_hong);
					Log.d("dodo", "收藏成功save");
				}else{
//					int isLike = likeMp3Info.getIsLike();
//					if(isLike ==1){
//						likeMp3Info.setIsLike(0);
						
						app.dbUtils.deleteById(Mp3Info.class,likeMp3Info.getId());
						imageView_favorite.setImageResource(R.mipmap.xin_bai);
						Log.d("dodo", "收藏删除");
//					}else{
//						likeMp3Info.setIsLike(1);
//						imageView_favorite.setImageResource(R.mipmap.xin_hong);

					}
//					app.dbUtils.update(likeMp3Info,"isLike");
//					Log.d("dododo", "isLike="+mp3Info.getIsLike());
//				}
			} catch (DbException e) {
				e.printStackTrace();
				Log.d("dodo", "错误");
			}
			break;
		}
		default:
			break;
		}
		
	}
	
	
	
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	if(fromUser){
		if(playService.isPause()){
			playService.pause();
			playService.seekTo(progress);
			imageButton2_play_pause.setImageResource(R.mipmap.zanting);
			playService.start();
		}else{
			playService.pause();
			playService.seekTo(progress);
			playService.start();
		}
	}
}
@Override
public void onStartTrackingTouch(SeekBar seekBar) {
	
}
@Override
public void onStopTrackingTouch(SeekBar seekBar) {
	
}
public class MyPagerAdapter extends PagerAdapter{
	@Override
	public int getCount() {
		return views.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		View v = views.get(position);
		container.addView(v);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}
	
}
@Override
public void onPageScrollStateChanged(int arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onPageScrolled(int arg0, float arg1, int arg2) {
	// TODO Auto-generated method stub
	
}
@Override
public void onPageSelected(int arg0) {
	// TODO Auto-generated method stub
	
}

}
