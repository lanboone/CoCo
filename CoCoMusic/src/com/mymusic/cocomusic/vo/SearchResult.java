package com.mymusic.cocomusic.vo;

public class SearchResult {
	private String MusicName;
	private String url;
	private String artist;
	private String album;
	public String getMusicName() {
		return MusicName;
	}
	public void setMusicName(String musicName) {
		MusicName = musicName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	@Override
	public String toString() {
		return "SearchResult [MusicName=" + MusicName + ", url=" + url
				+ ", artist=" + artist + ", album=" + album + "]";
	}
	

}
