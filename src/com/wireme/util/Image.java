package com.wireme.util;

import android.graphics.Bitmap;

public class Image {
	private int mImageID;					//�ļ�ID
	private String mFileName;				//�ļ���
	private String mImageName;				//ͼƬ���
	private String mFileSize;				//ͼƬ�ļ���С
	private Bitmap mThumbnail;				//ͼƬ����ͼ
	private String mFilePath;				//ͼƬ�ļ�·��
	
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
