package com.wireme.util;

import android.graphics.Bitmap;

/**
 * Song 
 * 
 * this is the message set for song
 * 
 * @author jianqiao-liu
 */

public class Audio {
	/**
	 * song id
	 */
	private int mSongID;
	
    /**
     * fileName
     * ��Ƶ�ļ���
     */
    private String mFileName = "";

    /**
     * song name
     * ������
     */
    private String mFileTitle = "";

    /**
     * play total time
     * ����ʱ��
     */
    private long mDuration = 0;

    /**
     * singer
     * �������
     */
    private String mSinger = "";

    /**
     * album name
     * ר�����
     */
    private String mAlbum = "";

    /**
     * album artwork
     * ר������
     */
    private Bitmap mAlbumPhoto = null;
    
    /**
     * mYear
     * �������
     */
    private String mYear = "";

    /**
     * mFileType
     * �ļ�����
     */
    private String mFileType = "";

    /**
     * mFileSize
     * �ļ���С
     */
    private String mFileSize = "";

    /**
     * mFilePath
     * �ļ�·��
     */
    private String mFilePath = "";

    /**
     * getSongID()
     * ��ȡ������MediaStore��ݿ��е�ID
     * @return
     */
    public int getmSongID(){
    	return mSongID;
    }
    
    /**
     * ���ø�����MediaStore��ݿ��е�ID
     * @param id
     */
    public void setmSongID(int id){
    	this.mSongID = id;
    }
    
    /**
     * getmFileName()
     * ��ȡ�ļ���
     * @return
     */
    public String getmFileName() {
        return mFileName;
    }

    /**
     * setmFileName()
     * �����ļ���
     * @param mFileName
     */
    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    /**
     * getmFileTitle()
     * ��ȡ������
     * @return
     */
    public String getmFileTitle() {
        return mFileTitle;
    }

    /**
     * setmFileTitle()
     * 
     * @param mFileTitle
     * ���ø�����
     */
    public void setmFileTitle(String mFileTitle) {
        this.mFileTitle = mFileTitle;
    }

    /**
     * getmDuration()
     * ��ȡ����ʱ��
     * @return
     */
    public long getmDuration() {
        return mDuration;
    }

    /**
     * setmDuration()
     * 
     * @param mDuration
     * ���ø���ʱ��
     */
    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    /**
     * getmSinger()
     * ��ȡ������
     * @return
     */
    public String getmSinger() {
        return mSinger;
    }

    /**
     * setmSinger()
     * ���ø�����
     * @param mSinger
     */
    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    /**
     * getmAlbum()
     * ��ȡר����
     * @return
     */
    public String getmAlbum() {
        return mAlbum;
    }

    /**
     * setmAlbum()
     * ����ר����
     * @param mAlbum
     */
    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    /**
     * ��ȡר������
     * @return
     */
    public Bitmap getmAlbumPhoto(){
    	return mAlbumPhoto;
    }
    
    /**
     * ����ר������
     * @param bitmap
     */
    public void setmAlbumPhoto(Bitmap bitmap){
    	this.mAlbumPhoto = bitmap;
    }
    
    /**
     * getmYear()
     * ��ȡ�������
     * @return
     */
    public String getmYear() {
        return mYear;
    }

    /**
     * setmYear()
     * ���÷������
     * @param mYear
     */
    public void setmYear(String mYear) {
        this.mYear = mYear;
    }

    /**
     * getmFileType()
     * ��ȡ�ļ�����
     * @return
     */
    public String getmFileType() {
        return mFileType;
    }

    /**
     * setmFileType()
     * �����ļ�����
     * @param mFileType
     */
    public void setmFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    /**
     * getmFileSize()
     * ��ȡ�ļ���С
     * @return
     */
    public String getmFileSize() {
        return mFileSize;
    }

    /**
     * setmFileSize()
     * �����ļ���С
     * @param mFileSize
     */
    public void setmFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    /**
     * getmFilePath()
     * ��ȡ�ļ�·��
     * @return
     */
    public String getmFilePath() {
        return mFilePath;
    }

    /**
     * setmFilePath()
     * �����ļ�·��
     * @param mFilePath
     */
    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }
}
