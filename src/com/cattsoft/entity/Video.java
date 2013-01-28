package com.cattsoft.entity;

import android.graphics.Bitmap;

public class Video {
	private int mVideoID;				//文件ID
	private String mFileName;			//文件名
	private String mFileTitle;			//视频名
	private long mDuration;				//视频时长
	private String mVideoDescription;	//视频描述
	private Bitmap mThumbnail;			//视频缩略图
	private String mFileSize;			//视频文件大小
	private String mFilePath;			//视屏文件路径
	
	public int getmVideoID() {
		return mVideoID;
	}
	
	public void setmVideoID(int mVideoID) {
		this.mVideoID = mVideoID;
	}
	
	public String getmFileName() {
		return mFileName;
	}
	
	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}
	
	public String getmFileTitle() {
		return mFileTitle;
	}
	
	public void setmFileTitle(String mFileTitle) {
		this.mFileTitle = mFileTitle;
	}
	
	public long getmDuration() {
		return mDuration;
	}
	
	public void setmDuration(long mDuration) {
		this.mDuration = mDuration;
	}
	
	public String getmVideoDescription(){
		return this.mVideoDescription;
	}
	
	public void setmVideoDescription(String mVideoDescription){
		this.mVideoDescription = mVideoDescription;
	}
	
	public Bitmap getmThumbnail() {
		return mThumbnail;
	}
	
	public void setmThumbnail(Bitmap mThumbnail) {
		this.mThumbnail = mThumbnail;
	}
	
	public String getmFileSize() {
		return mFileSize;
	}
	
	public void setmFileSize(String mFileSize) {
		this.mFileSize = mFileSize;
	}
	
	public String getmFilePath() {
		return mFilePath;
	}
	
	public void setmFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}
}
