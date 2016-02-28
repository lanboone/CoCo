package com.mymusic.cocomusic.util;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.mymusic.cocomusic.R;
import com.mymusic.cocomusic.vo.Mp3Info;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

public class MediaUtils {
	
	//��ȡר�������Uri
	private static final Uri albumArtUri = Uri
			.parse("content://media/external/audio/albumart");
	public static Mp3Info getMp3Info(Context context,long _id){
		
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, 
				MediaStore.Audio.Media._ID+"="+_id, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		Mp3Info mp3info =null;
		if(cursor.moveToNext()){
			mp3info = new Mp3Info();
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));//����id
			String title = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE));//���ֱ���
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));//������
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));//ר��
			long albumId = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			long duration = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));//ʱ��
			long size = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE));//�ļ���С
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));//�ļ�·��
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//�Ƿ�Ϊ����
			if(isMusic !=0){//ֻ��������ӵ����ϵ���
				mp3info.setId(id);
				mp3info.setTitle(title);
				mp3info.setArtist(artist);
				mp3info.setAlbum(album);
				mp3info.setAlbumId(albumId);
				mp3info.setDuration(duration);
				mp3info.setSize(size);
				mp3info.setUrl(url);
			}
		}
		cursor.close();
		return mp3info;
	}
	/*
	 * ���ڴ����ݿ��в�ѯ��������Ϣ��������List����
	 */
	public static long[] getMp3InfoIds(Context context){
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[]{MediaStore.Audio.Media._ID}, 
				MediaStore.Audio.Media.DURATION + ">=180000", null, 
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		long[] ids = null;
		if (cursor != null){
			ids = new long[cursor.getCount()];
			for(int i = 0 ;i < cursor.getCount();i++){
				cursor.moveToNext();
				ids[i]=cursor.getLong(0);
			}
		}
		cursor.close();
		
		return ids;
		
	}
	/*
	 * ���ڴ����ݿ��в�ѯ��������Ϣ��������List����
	 */
	public static ArrayList<Mp3Info> getMp3Infos(Context context){
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, 
				MediaStore.Audio.Media.DURATION + ">=180000", null, 
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		ArrayList<Mp3Info> mp3infos =new ArrayList<Mp3Info>();
		for(int i = 0;i<cursor.getCount();i++){
			cursor.moveToNext();
			Mp3Info mp3info =new Mp3Info();
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));//����id
			String title = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE));//���ֱ���
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));//������
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));//ר��
			long albumId = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			long duration = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));//ʱ��
			long size = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE));//�ļ���С
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));//�ļ�·��
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//�Ƿ�Ϊ����
			if(isMusic !=0){//ֻ��������ӵ����ϵ���
				mp3info.setId(id);
				mp3info.setTitle(title);
				mp3info.setArtist(artist);
				mp3info.setAlbum(album);
				mp3info.setAlbumId(albumId);
				mp3info.setDuration(duration);
				mp3info.setSize(size);
				mp3info.setUrl(url);
				mp3infos.add(mp3info);
			}
		}
		cursor.close();
		
		return mp3infos;
		
	}
	/**
	 * ��ʽ��ʱ��
	 */
	public static String formatTime(long time){
		String min = time/(1000*60)+"";
		String sec = time%(1000*60)+"";
		if(min.length()<2){
			min="0" +time/(1000*60)+"";
			
		}else{
			min = time/(1000*60)+"";
			
		}
		if(sec.length() == 4){
			sec = "0"+(time%(1000*60))+"";
		}else if(sec.length() == 3){
			sec = "00"+(time%(1000*60))+"";
		}else if(sec.length() == 2){
			sec = "000"+(time%(1000*60))+"";
		}else if(sec.length() == 1){
			sec = "0000"+(time%(1000*60))+"";
		}	
	return min + ":"+sec.trim().substring(0,2);
	}
	
	public static Bitmap getDefaultArtwork(Context context, boolean small){
		Options opts = new Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		if(small){//����СͼƬ
			return BitmapFactory.decodeStream(context.getResources()
					.openRawResource(R.mipmap.app_logo2),null, opts);
			
		}
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.mipmap.music_album),null, opts);
	}
	/*
	 * ���ļ����л�ȡר������λͼ
	 */
	private static Bitmap getArtworkFromFile(Context context,long song_id,
			                                  long albumid){
		Bitmap bm = null;
		if(albumid <0 &&song_id<0){
			throw new IllegalArgumentException("Must specify an album or a song id");
		}
		try{
			Options options = new Options();
			FileDescriptor fd = null;
			if(albumid < 0){
				Uri uri = Uri.parse("content://media/external/audio/media"+song_id+"/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if(pfd != null){
					fd = pfd.getFileDescriptor();
				}
			}else{
				Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if(pfd != null){
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			//ֻ���д�С�ж�
			options.inJustDecodeBounds = true;
			//���ô˷����õ�options�õ�ͼƬ�Ĵ�С
			BitmapFactory.decodeFileDescriptor(fd,null, options);
			//���ǵ�Ŀ������800pixel�Ļ�������ʾ
			//������Ҫ����computeSampleSize�õ�ͼƬ���ŵı���
			options.inSampleSize=100;
			//���ǵõ������ŵı��������ڿ�ʼ��ʽ����Bitmap����
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			//����options��������������Ҫ���ڴ�
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return bm;
		
	}
	
	
	
	
	/*
	 * ��ȡר������λͼ����
	 */
	public static Bitmap getArtwork(Context context,long song_id,
			long album_id,boolean allowdefault,boolean small){
		if(album_id<0){
			if(song_id<0){
				Bitmap bm = getArtworkFromFile(context,song_id,-1);
				if(bm !=null){
					return bm;
				}
			}
			if(allowdefault){
				return getDefaultArtwork(context, small);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
		if(uri != null){
			InputStream in = null;
			try{
			in=res.openInputStream(uri);
			Options options = new Options();
			//���ƶ�ԭʼ��С
			options.inSampleSize = 1;
			//ֻ���д�С�ж�
			options.inJustDecodeBounds = true;
			//���ô˷����õ�options�õ�ͼƬ�Ĵ�С
			BitmapFactory.decodeStream(in, null, options);
			//���ǵ�Ŀ��������N pixel�Ļ�������ʾ��������Ҫ����computeSampleSize�õ�ͼƬ���ŵı���
			//�����targetΪ800�Ǹ���Ĭ��ר��ͼƬ��С�����ģ�800ֻ�ǲ������ֵ���������������Ľ��
			if(small){
				options.inSampleSize = computeSampleSize(options,40);
			}else{
				options.inSampleSize = computeSampleSize(options,600);
			}
			//���ǵõ������ű��������ڿ�ʼ��ʽ����Bitmap����
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			in = res.openInputStream(uri);
			return BitmapFactory.decodeStream(in, null, options);
		}catch(FileNotFoundException e){
			Bitmap bm = getArtworkFromFile(context,song_id,album_id);
			if(bm!=null){
				if(bm.getConfig() == null){
					bm = bm.copy(Bitmap.Config.RGB_565, false);
					if(bm == null&&allowdefault){
						return getDefaultArtwork(context,small);
					}
				}
			}else if(allowdefault){
				bm = getDefaultArtwork(context,small);
			}
			return bm;
		}finally{
			try{
				if(in!=null){
					in.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		}	
	return null;
		
	}
	private static int computeSampleSize(Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		
		if(candidate == 0){
			return 1;
		}
		if(candidate >1){
			if((w>target)&&(w/candidate)<target){
				candidate -= 1;
			}
		}
		if(candidate > 1){
			if((h>target)&&(h/candidate)<target){
				candidate -= 1;
			}
		}
		
		return candidate;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
