package com.cattsoft.util;

import java.util.ArrayList;
import com.cattsoft.entity.Video;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class UtilVideoInfo {
	private ArrayList<Video> mVideoList = new ArrayList<Video>();
	private Context mContext;
	private Builder builder;
	private AlertDialog alert;
	
	public String toTime(long time) {

		time /= 1000;
		long minute = time / 60;
		long second = time % 60;
		return String.format("%02d:%02d", minute, second);
	}
	
	public UtilVideoInfo(Context context){
		mContext = context;
	}
	
	public ArrayList<Video> getVideoList(){
		Video video = null;
		Cursor c = mContext.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Video.Media._ID,
						MediaStore.Video.Media.DISPLAY_NAME,
						MediaStore.Video.Media.TITLE,
						MediaStore.Video.Media.DURATION,
						MediaStore.Video.Media.SIZE,
						MediaStore.Video.Media.DESCRIPTION,
						MediaStore.Video.Media.DATA }, null, null, null);
		if (c == null || c.getCount() == 0) {
			builder = new AlertDialog.Builder(mContext);
			builder.setMessage("存储列表为空...").setPositiveButton("确定", null);
			alert = builder.create();
			alert.show();
		}
		
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			video = new Video();
			video.setmVideoID(c.getInt(0));//id
			video.setmFileName(c.getString(1));//file name
			video.setmFileTitle(c.getString(2));//video name
			video.setmDuration(c.getLong(3));//duration
			if (c.getString(4) != null) {// fileSize
				float temp = c.getInt(4) / 1024f / 1024f;
				String sizeStr = (temp + "").substring(0, 4);
				video.setmFileSize(sizeStr + "M");
			} else {
				video.setmFileSize("undefine");
			}
			if (c.getString(5) != null){//description
				video.setmVideoDescription(c.getString(5));
			} else {
				video.setmVideoDescription("undefine");
			}
			if (c.getString(6) != null) {// file path
				video.setmFilePath(c.getString(6));
			}
			
			//设置视频缩略图
			video.setmThumbnail(getVideoThumbnail(c.getString(6), 100, 60, MediaStore.Images.Thumbnails.MICRO_KIND));
			mVideoList.add(video);
			c.moveToNext();
		}
		
		System.out.println("共有" + mVideoList.size() + "个视频");
		return mVideoList;
	}

	/**
	 * 获取视频的缩略图
	 * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * @param videoPath 视频的路径
	 * @param width 指定输出视频缩略图的宽度
	 * @param height 指定输出视频缩略图的高度度
	 * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		if(bitmap != null){
			System.out.println("w"+bitmap.getWidth());
			System.out.println("h"+bitmap.getHeight());
		}
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}
