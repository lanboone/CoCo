package com.mymusic.cocomusic.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.mymusic.cocomusic.vo.SearchResult;

public class SearchMusicUtils {
	private static final int SIZE = 20;//��ѯǰ��20������
	private static final String URL =Constent.BAIDU_URL +Constent.BAIDU_SEARCH;
	private static SearchMusicUtils sInstance;
	private OnSearchResultListener mListener;
	private ExecutorService mThreadPool;
	
	public synchronized static SearchMusicUtils getInstance(){
		if(sInstance == null){
			try {
				sInstance = new SearchMusicUtils();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		return sInstance;
		
	}
	private SearchMusicUtils() throws ParserConfigurationException{
		mThreadPool = Executors.newSingleThreadExecutor();
	}
	public SearchMusicUtils setListener(OnSearchResultListener l){
		mListener = l;
		return this;
		
	}
	public void search(final String key,final int page){
		final Handler handler = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constent.SUCCESS:
					if(mListener != null)mListener.onSearchResult((ArrayList<SearchResult>)msg.obj);
					break;
				case Constent.FAILED:
					if(mListener != null)mListener.onSearchResult(null);
					break;

				default:
					break;
				}
			}
		};
	
	mThreadPool.execute(new Runnable(){
		public void run(){
			ArrayList<SearchResult> results = getMusicList(key,page);
			if(results == null){
				handler.sendEmptyMessage(Constent.FAILED);
				return;
			}
			handler.obtainMessage(Constent.SUCCESS,results).sendToTarget();
		}
	});
	}
	//ʹ��Jsoup���������������
	private ArrayList<SearchResult> getMusicList(final String key,final int page){
		final String start = String.valueOf((page - 1)*SIZE);
		try {
			Document doc = Jsoup.connect(URL).data("key",key,"start",start,"size",String.valueOf(SIZE))
					.userAgent(Constent.USER_AGENT).timeout(60*1000).get();
			Log.d("dodo", doc+"");
			Elements songTitles = doc.select("div.song-item.clearfix");
			Elements songInfos;
			ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
			
			TAG:
			for(Element song : songTitles){
				songInfos = song.getElementsByTag("a");
				SearchResult searchResult = new SearchResult();
				for(Element info :songInfos){
					//�շѵĸ���
					if(info.attr("href").startsWith("http://y.baidu.com/song/")){
						continue TAG;
					}
					
					//��ת���ٶ����ֺеĸ���
					if(info.attr("href").equals("#")&& !TextUtils.isEmpty(info.attr("data-songdata"))){
						continue TAG;
					}
					//��������
					if(info.attr("href").startsWith("/song")){
						searchResult.setMusicName(info.text());
						searchResult.setUrl(info.attr("href"));
					}
					//��������
					if(info.attr("href").startsWith("/data")){
						searchResult.setArtist(info.text());
					}
					//ר������
					if(info.attr("href").startsWith("/album")){
						searchResult.setAlbum(info.text().replaceAll("��|��", ""));
					}
				}
				searchResults.add(searchResult);
			}
			Log.d("dodo", searchResults+"");
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		
		return null;
		
	}
	public interface OnSearchResultListener{
		public void onSearchResult(ArrayList<SearchResult> results);
	}

}
