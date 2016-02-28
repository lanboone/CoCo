package com.mymusic.cocomusic;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.exception.DbException;
import com.mymusic.cocomusic.adapter.MyMusicListAdapter;
import com.mymusic.cocomusic.vo.Mp3Info;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyLikeMusicListActivity extends BaseActivity implements OnItemClickListener{
	private ListView listView_like;
	private CoCoMusicApp app;
	private ArrayList<Mp3Info> likeMp3Infos;
	private MyMusicListAdapter adapter;
	private boolean isChange = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_like_music_list);
		app = (CoCoMusicApp) getApplication();
		listView_like = (ListView) findViewById(R.id.listView_like);
		listView_like.setOnItemClickListener(this);

		initData();
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
	

	private void initData() {
		
		

		try {
			List<Mp3Info> list = app.dbUtils.findAll(Mp3Info.class);
			if(list == null||list.size()==0){
				return;
			}
			
				likeMp3Infos = (ArrayList<Mp3Info>)list;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter = new MyMusicListAdapter(this,likeMp3Infos);
			listView_like.setAdapter(adapter);
//			listView_like.setOnItemClickListener(this);

	}

	@Override
	public void publish(int progress) {
		
	}

	@Override
	public void change(int position) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		if(playService.getChangePlayList()!=PlayService.LIKE_MUSIC_LIST){
			playService.setMp3Infos(likeMp3Infos);
			
			playService.setChangePlayList(PlayService.LIKE_MUSIC_LIST);
			
		}
		playService.play(position);
		
//		savePlayRecord();
	}
//	private void savePlayRecord() {
//		//获取当前正在播放的音乐对象
//		Mp3Info mp3Info = playService.getMp3Infos().get(playService.getCurrentPosition());
//		try{
//		
//		Mp3Info playRecordMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getMp3InfoIdd()));
//		if(playRecordMp3Info == null){
//			mp3Info.setPlayTime(System.currentTimeMillis());
//			app.dbUtils.save(mp3Info);
//			
//		}else{
//			playRecordMp3Info.setPlayTime(System.currentTimeMillis());
//			app.dbUtils.update(playRecordMp3Info, "playTime");
//		}
//	}catch(DbException e){
//		e.printStackTrace();
//	}
//	}

}
