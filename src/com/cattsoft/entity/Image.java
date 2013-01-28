package com.cattsoft.entity;

import android.graphics.Bitmap;

public class Image {
	private int mImageID;					//文件ID
	private String mFileName;				//文件名
	private String mImageName;				//图片名称
	private String mFileSize;				//图片文件大小
	private Bitmap mThumbnail;				//图片缩略图
	private String mFilePath;				//图片文件路径
	
	public int getmImageID() {
		return mImageID;
	}
	
	public void setmImageID(int mImageID) {
		this.mImageID = mImageID;
	}
	
	public String getmFileName() {
		return mFileName;
	}
	
	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}
	
	public String getmImageName() {
		return mImageName;
	}
	
	public void setmImageName(String mImageName) {
		this.mImageName = mImageName;
	}
	
	public String getmFileSize() {
		return mFileSize;
	}
	
	public void setmFileSize(String mFileSize) {
		this.mFileSize = mFileSize;
	}
	
	public Bitmap getmThumbnail() {
		return mThumbnail;
	}
	
	public void setmThumbnail(Bitmap mThumbnail) {
		this.mThumbnail = mThumbnail;
	}
	
	public String getmFilePath() {
		return mFilePath;
	}
	
	public void setmFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}
}
