package com.cattsoft.entity;

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
     * 音频文件名
     */
    private String mFileName = "";

    /**
     * song name
     * 歌曲名
     */
    private String mFileTitle = "";

    /**
     * play total time
     * 歌曲时长
     */
    private long mDuration = 0;

    /**
     * singer
     * 歌手名称
     */
    private String mSinger = "";

    /**
     * album name
     * 专辑名称
     */
    private String mAlbum = "";

    /**
     * album artwork
     * 专辑封面
     */
    private Bitmap mAlbumPhoto = null;
    
    /**
     * mYear
     * 发行年份
     */
    private String mYear = "";

    /**
     * mFileType
     * 文件类型
     */
    private String mFileType = "";

    /**
     * mFileSize
     * 文件大小
     */
    private String mFileSize = "";

    /**
     * mFilePath
     * 文件路径
     */
    private String mFilePath = "";

    /**
     * getSongID()
     * 获取歌曲在MediaStore数据库中的ID
     * @return
     */
    public int getmSongID(){
    	return mSongID;
    }
    
    /**
     * 设置歌曲在MediaStore数据库中的ID
     * @param id
     */
    public void setmSongID(int id){
    	this.mSongID = id;
    }
    
    /**
     * getmFileName()
     * 获取文件名
     * @return
     */
    public String getmFileName() {
        return mFileName;
    }

    /**
     * setmFileName()
     * 设置文件名
     * @param mFileName
     */
    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    /**
     * getmFileTitle()
     * 获取歌曲名
     * @return
     */
    public String getmFileTitle() {
        return mFileTitle;
    }

    /**
     * setmFileTitle()
     * 
     * @param mFileTitle
     * 设置歌曲名
     */
    public void setmFileTitle(String mFileTitle) {
        this.mFileTitle = mFileTitle;
    }

    /**
     * getmDuration()
     * 获取歌曲时长
     * @return
     */
    public long getmDuration() {
        return mDuration;
    }

    /**
     * setmDuration()
     * 
     * @param mDuration
     * 设置歌曲时长
     */
    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    /**
     * getmSinger()
     * 获取歌手名
     * @return
     */
    public String getmSinger() {
        return mSinger;
    }

    /**
     * setmSinger()
     * 设置歌手名
     * @param mSinger
     */
    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    /**
     * getmAlbum()
     * 获取专辑名
     * @return
     */
    public String getmAlbum() {
        return mAlbum;
    }

    /**
     * setmAlbum()
     * 设置专辑名
     * @param mAlbum
     */
    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    /**
     * 获取专辑封面
     * @return
     */
    public Bitmap getmAlbumPhoto(){
    	return mAlbumPhoto;
    }
    
    /**
     * 设置专辑封面
     * @param bitmap
     */
    public void setmAlbumPhoto(Bitmap bitmap){
    	this.mAlbumPhoto = bitmap;
    }
    
    /**
     * getmYear()
     * 获取发行年份
     * @return
     */
    public String getmYear() {
        return mYear;
    }

    /**
     * setmYear()
     * 设置发行年份
     * @param mYear
     */
    public void setmYear(String mYear) {
        this.mYear = mYear;
    }

    /**
     * getmFileType()
     * 获取文件类型
     * @return
     */
    public String getmFileType() {
        return mFileType;
    }

    /**
     * setmFileType()
     * 设置文件类型
     * @param mFileType
     */
    public void setmFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    /**
     * getmFileSize()
     * 获取文件大小
     * @return
     */
    public String getmFileSize() {
        return mFileSize;
    }

    /**
     * setmFileSize()
     * 设置文件大小
     * @param mFileSize
     */
    public void setmFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    /**
     * getmFilePath()
     * 获取文件路径
     * @return
     */
    public String getmFilePath() {
        return mFilePath;
    }

    /**
     * setmFilePath()
     * 设置文件路径
     * @param mFilePath
     */
    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }
}
