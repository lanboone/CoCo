package com.mymusic.cocomusic;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mymusic.cocomusic.adapter.NetMusicAdapter;
import com.mymusic.cocomusic.util.AppUtils;
import com.mymusic.cocomusic.util.Constent;
import com.mymusic.cocomusic.util.SearchMusicUtils;
import com.mymusic.cocomusic.vo.SearchResult;

import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
/*
 * 提示：由于百度页面可能更新解析音乐数据时请按实际页面编写解析代码
 */


public class NetMusicListFragment extends Fragment implements OnClickListener,OnItemClickListener{
	private MainActivity mainActivity;
	private ListView listView_net_music;
	private LinearLayout load_layout;
	private LinearLayout ll_search_container;
	private LinearLayout ll_search_btn_container;
	private ImageButton ib_search_btn;
	private EditText et_search_content;
	private ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
	private NetMusicAdapter netMusicAdapter;
	private int page = 1;//搜索音乐的页码

	public static NetMusicListFragment newInstance(){
		NetMusicListFragment net = new NetMusicListFragment();
		return net;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = (MainActivity) getActivity();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//UI组件的初始化
		View view = inflater.inflate(R.layout.net_music_list, null);
		listView_net_music = (ListView) view.findViewById(R.id.listView_net_music);
		load_layout = (LinearLayout) view.findViewById(R.id.load_layout);
		ll_search_container= (LinearLayout) view.findViewById(R.id.ll_search_container);
		ll_search_btn_container = (LinearLayout) view.findViewById(R.id.ll_search_btn_container);
		ib_search_btn = (ImageButton) view.findViewById(R.id.ib_search_btn);
		et_search_content = (EditText) view.findViewById(R.id.et_search_content);
		
		listView_net_music.setOnItemClickListener(this);
		ll_search_btn_container.setOnClickListener(this);
		ib_search_btn.setOnClickListener(this);
//		loadNetData();//加载网络音乐
//		getdoc();
		
		return view;
	}
//	public void getdoc(){
//		String url = "http://music.baidu.com/top/dayhot/?pst=shouyeTop";
//		try {
//			Document doc = Jsoup.connect(url).userAgent(Constent.USER_AGENT).timeout(6*1000).get();
//			Log.d("dooooo", doc+"..................");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	private void loadNetData() {
		load_layout.setVisibility(View.VISIBLE);
		//执行异步加载网络音乐的任务
		new LoadNetDataTask().execute(Constent.BAIDU_URL+Constent.BAIDU_DAYHOT);
	}
	public void changeUIStatus(int position){
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_search_btn_container:
			ll_search_btn_container.setVisibility(View.GONE);
			ll_search_container.setVisibility(View.VISIBLE);
			
			break;
		case R.id.ib_search_btn:
			searchMusic();
			break;

		default:
			break;
		}
	}
	//搜索音乐
	private void searchMusic() {
		//隐藏输入法
		AppUtils.hideInputMethod(et_search_content);
		ll_search_btn_container.setVisibility(View.VISIBLE);
		ll_search_container.setVisibility(View.GONE);
		String key = et_search_content.getText().toString();
		if(TextUtils.isEmpty(key)){
			Toast.makeText(mainActivity, "请输入关键字", Toast.LENGTH_SHORT).show();
			return;
		}
		load_layout.setVisibility(View.VISIBLE);
		//因为涉及到事件，自己写回调方法，自己写监听事件，这是观察者设计模式
		SearchMusicUtils.getInstance().setListener(new SearchMusicUtils.OnSearchResultListener() {
			
			
			@Override
			public void onSearchResult(ArrayList<SearchResult> results) {
				ArrayList<SearchResult> sr = netMusicAdapter.getSearchResults();
				sr.clear();
				sr.addAll(results);
				netMusicAdapter.notifyDataSetChanged();
				load_layout.setVisibility(View.GONE);
			}
		}).search(key, page);
		
	}
	/*
	 * 加载网络音乐的异步任务
	 */
	class LoadNetDataTask extends AsyncTask<String,Integer,Integer>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			load_layout.setVisibility(View.VISIBLE);
			listView_net_music.setVisibility(View.GONE);
			searchResults.clear();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String url = params[0];
			//使用Jsoup组件请求网络，并解析音乐数据
			
			try {
				Document doc = Jsoup.connect(url).userAgent(Constent.USER_AGENT).timeout(10*1000).get();
			Log.d("gogo",doc+"-------------------------------------------------");
			Elements songTitles = doc.select("span.song-title");
			Elements artists = doc.select("span.author_list");
			//Log.d("doooooo", songTitles.size()+"..........");
			for(int i = 0;i<60;i++){
				//Log.d("doooooo", songTitles.size()+"..........");
				SearchResult searchResult = new SearchResult();
				Elements urls = songTitles.get(i).getElementsByTag("a");
				searchResult.setUrl(urls.get(0).attr("href"));
				searchResult.setMusicName(urls.get(0).text());
				Elements artistElements = artists.get(i).getElementsByTag("a");
				searchResult.setArtist(artistElements.get(0).text());
				
				searchResult.setAlbum("热歌榜");
				searchResults.add(searchResult);
				
			}
			Log.d("gogo",searchResults+"---------123123");
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			return 1;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
//			if(result == 1){
				netMusicAdapter = new NetMusicAdapter(mainActivity,searchResults);
				listView_net_music.setAdapter(netMusicAdapter);
				listView_net_music.addFooterView(LayoutInflater.from(mainActivity).inflate(R.layout.footview_layout, null));
//			}
			load_layout.setVisibility(View.GONE);
			listView_net_music.setVisibility(View.VISIBLE);
		}
		
	}
}
