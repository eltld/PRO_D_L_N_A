package com.cattsoft.util;

import android.graphics.Bitmap;

public interface UtilInterf {
	/**
	 * 通过路径查看图片
	 * @param path
	 * @return
	 */
	public Bitmap CheckPictureByPath(String path);
	
	/**
	 * 通过网络地址或Uri查看图片
	 * @param uri
	 * @return
	 */
	public Bitmap CheckPictureByUri(String uri);
	
	/**
	 * 通过路径查看音频
	 * @param path
	 * @return
	 */
	public String CheckAudioByPath(String path);
	
	/**
	 * 通过网络地址或Uri查看音频
	 * @param uri
	 * @return
	 */
	public String CheckAudioByUri(String uri);
	
	/**
	 * 通过路径查看视频
	 * @param path
	 * @return
	 */
	public boolean CheckVideoByPath(String path);
	
	/**
	 * 通过网络地址或Uri查看视频
	 * @param uri
	 * @return
	 */
	public boolean CheckVideoByUri(String uri);
}
