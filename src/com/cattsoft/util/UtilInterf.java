package com.cattsoft.util;

import android.graphics.Bitmap;

public interface UtilInterf {
	/**
	 * ͨ��·���鿴ͼƬ
	 * @param path
	 * @return
	 */
	public Bitmap CheckPictureByPath(String path);
	
	/**
	 * ͨ�������ַ��Uri�鿴ͼƬ
	 * @param uri
	 * @return
	 */
	public Bitmap CheckPictureByUri(String uri);
	
	/**
	 * ͨ��·���鿴��Ƶ
	 * @param path
	 * @return
	 */
	public String CheckAudioByPath(String path);
	
	/**
	 * ͨ�������ַ��Uri�鿴��Ƶ
	 * @param uri
	 * @return
	 */
	public String CheckAudioByUri(String uri);
	
	/**
	 * ͨ��·���鿴��Ƶ
	 * @param path
	 * @return
	 */
	public boolean CheckVideoByPath(String path);
	
	/**
	 * ͨ�������ַ��Uri�鿴��Ƶ
	 * @param uri
	 * @return
	 */
	public boolean CheckVideoByUri(String uri);
}
