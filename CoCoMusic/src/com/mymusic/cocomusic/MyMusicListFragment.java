package com.mymusic.cocomusic;

import java.util.ArrayList;

import quickscroll.QuickScroll;
import com.mymusic.cocomusic.adapter.MyMusicListAdapter;
import com.mymusic.cocomusic.util.MediaUtils;
import com.mymusic.cocomusic.vo.Mp3Info;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyMusicListFragment extends Fragment implements OnItemClickListener,OnClickListener{

	private ListView listView_my_music;
	private ArrayList<Mp3Info> mp3Infos;
	private MainActivity mainActivity;
	private MyMusicListAdapter musicListAdapter;
	private ImageView imageView_album;
	private ImageView iv_play_pause;
	private ImageView iv_next;
	private TextView tv_songName;
	private TextView tv_songHand;
	private QuickScroll quickScroll;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = (MainActivity) activity;
	}
	public static MyMusicListFragment newInstance(){
		MyMusicListFragment my = new MyMusicListFragment();
		return my;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		dbUtils = DbUtils.create(mainActivity, Constent.DB_NAME);
		
		View view = inflater.inflate(R.layout.mymusic_list_layout, null);
		listView_my_music = (ListView) view.findViewById(R.id.listView_my_music);
		imageView_album = (ImageView) view.findViewById(R.id.imageView_album);
		tv_songName = (TextView) view.findViewById(R.id.tv_songName);
		tv_songHand = (TextView) view.findViewById(R.id.tv_songHand);
		iv_play_pause = (ImageView) view.findViewById(R.id.iv_play_pause);
		iv_next = (ImageView) view.findViewById(R.id.iv_next);
		quickScroll = (QuickScroll) view.findViewById(R.id.quickscroll);
		listView_my_music.setOnItemClickListener(this);
		iv_play_pause.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		imageView_album.setOnClickListener(this);
		
		
		
		
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		
		//绑定播放服务
		mainActivity.bindPlayService();

	}
	@Override
	public void onPause() {
		super.onPause();
		//解绑
		mainActivity.unbindPlayService();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		
	}
	/*
	 * 加载本地音乐列表
	 */
	public void loadData() {
		mp3Infos = MediaUtils.getMp3Infos(mainActivity);
		musicListAdapter = new MyMusicListAdapter(mainActivity,mp3Infos);
		listView_my_music.setAdapter(musicListAdapter);
		initQuickscroll();
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mainActivity.playService.getChangePlayList()!=PlayService.MY_MUSIC_LIST){
			mainActivity.playService.setMp3Infos(mp3Infos);
			mainActivity.playService.setChangePlayList(PlayService.MY_MUSIC_LIST);
		}
		mainActivity.playService.play(position);
		//保存播放时间
//		savePlayRecord();
	}
	
//	//保存播放记录
//	private void savePlayRecord(){
//		
//		Mp3Info mp3Info = mainActivity.playService.getMp3Infos().get(mainActivity.playService.getCurrentPosition());
//		Log.d("dododo",mp3Info+ "myMusicListFragment");
//		try{
//		Mp3Info playRecordMp3Info = mainActivity.app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
//		
//		if(playRecordMp3Info == null){
//			
//			
////			mp3Info.setMp3InfoId(mp3Info.getId());
//			mp3Info.setPlayTime(System.currentTimeMillis());//设置当前系统时间
//			mainActivity.app.dbUtils.save(mp3Info);	
//			Log.d("dododo", mp3Info.getMp3InfoIdd()+"MyMusicListFragment.......");
//		}else{
//			playRecordMp3Info.setPlayTime(System.currentTimeMillis());
//			mainActivity.app.dbUtils.update(playRecordMp3Info, "playTime");
//		}
//	}catch(DbException e){
//		e.printStackTrace();
//		Log.d("dododo","失败");
//	}
//}
	//回调播放状态的UI设置
	public void changeUIStatusOnPlay(int position){
		if(position>=0&&position<mainActivity.playService.mp3Infos.size()){
			Mp3Info mp3Info = mainActivity.playService.mp3Infos.get(position);
			tv_songName.setText(mp3Info.getTitle());
			tv_songHand.setText(mp3Info.getArtist());
			if(mainActivity.playService.isPlaying()){
				iv_play_pause.setImageResource(R.mipmap.zanting);
			}else {
				iv_play_pause.setImageResource(R.mipmap.bofang);
			}			
			Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
			imageView_album.setImageBitmap(albumBitmap);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_play_pause:{
			if(mainActivity.playService.isPlaying()){
				iv_play_pause.setImageResource(R.mipmap.bofang);
				mainActivity.playService.pause();

			}else{
				if(mainActivity.playService.isPause()){
					iv_play_pause.setImageResource(R.mipmap.zanting);
					mainActivity.playService.start();
				}else{
					mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
				}				
			}
			break;
		}
		case R.id.iv_next:
			mainActivity.playService.next();
			break;
		case R.id.imageView_album:
			Intent intent = new Intent(mainActivity,PlayActivity.class);
//			intent.putExtra("isPause", isPause);
			startActivity(intent);
			break;
			default:
				break;
		}
	}
	private void initQuickscroll(){
		quickScroll.init(QuickScroll.TYPE_POPUP_WITH_HANDLE, listView_my_music, musicListAdapter, QuickScroll.STYLE_HOLO);
		quickScroll.setFixedSize(1);
		Log.d("dododo", "ad3");
		quickScroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
		quickScroll.setPopupColor(QuickScroll.BLUE_LIGHT, QuickScroll.BLUE_LIGHT_SEMITRANSPARENT,1 ,Color.WHITE, 1);
	}
}
