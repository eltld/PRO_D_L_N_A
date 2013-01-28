package com.cattsoft.entity;

import android.graphics.Bitmap;

public class Video {
	private int mVideoID;				//�ļ�ID
	private String mFileName;			//�ļ���
	private String mFileTitle;			//��Ƶ��
	private long mDuration;				//��Ƶʱ��
	private String mVideoDescription;	//��Ƶ����
	private Bitmap mThumbnail;			//��Ƶ����ͼ
	private String mFileSize;			//��Ƶ�ļ���С
	private String mFilePath;			//�����ļ�·��
	
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
