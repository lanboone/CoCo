package com.mymusic.cocomusic.vo;

public class Mp3Info {

	private long id;
	
	
	private long mp3InfoIdd;//���ղ�����ʱ���ڱ���ԭʼID
	private long playTime;//�������ʱ��
//	private int isLike;//1ϲ�� 0Ĭ��
	private String title;//����
	private String artist;//������
	private String album;//ר��
	private long albumId;//
	private long duration;//ʱ��
	private long size;//��С
	private String url;//·��
	private boolean isMusic;//�Ƿ�Ϊ����
	public long getPlayTime() {
		return playTime;
	}
	public void setPlayTime(long playTime) {
		this.playTime = playTime;
	}
//	public int getIsLike() {
//		return isLike;
//	}
//	public void setIsLike(int isLike) {
//		this.isLike = isLike;
//	}
	public long getMp3InfoIdd() {
		return mp3InfoIdd;
	}
	public void setMp3InfoIdd(long mp3InfoIdd) {
		this.mp3InfoIdd = mp3InfoIdd;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public long getAlbumId() {
		return albumId;
	}
	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isMusic() {
		return isMusic;
	}
	public void setMusic(boolean isMusic) {
		this.isMusic = isMusic;
	}
	@Override
	public String toString() {
		return "Mp3info [id=" + id + ", title=" + title + ", artist=" + artist
				+ ", album=" + album + ", albumId=" + albumId + ", duration="
				+ duration + ", size=" + size + ", url=" + url + ", isMusic="
				+ isMusic + "]";
	}
	
	
}
