package com.mymusic.cocomusic.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mymusic.cocomusic.BaseActivity;
import com.mymusic.cocomusic.R;
import com.mymusic.cocomusic.vo.SearchResult;

public class NetMusicAdapter extends BaseAdapter{
	private Context context;

	private ArrayList<SearchResult> searchResults;
	public NetMusicAdapter(Context context,ArrayList<SearchResult> searchResults){
		this.context = context;
		this.searchResults = searchResults;
	}
	
	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(ArrayList<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}

	@Override
	public int getCount() {
		return searchResults.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchResults.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView==null){
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.net_item_music_list, null);
			vh.net_item_tv_songName = (TextView) convertView.findViewById(R.id.net_item_tv_songName);
			vh.net_item_tv_songHand = (TextView) convertView.findViewById(R.id.net_item_tv_songHand);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		SearchResult searchResult = searchResults.get(position);
		vh.net_item_tv_songName.setText(searchResult.getMusicName());
		vh.net_item_tv_songHand.setText(searchResult.getArtist());
		return convertView;
	}
	class ViewHolder{
		TextView net_item_tv_songName;
		TextView net_item_tv_songHand;
	}

}
