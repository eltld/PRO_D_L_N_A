package com.cattsoft.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class UtilImpl implements UtilInterf {

	/**
	 * ͨ��·���鿴ͼƬ
	 * @param path
	 * @return
	 */
	public Bitmap CheckPictureByPath(String path) {
		// TODO Auto-generated method stub
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		return bitmap;
	}

	/**
	 * ͨ
	 * @param uri
	 * @return
	 */
	public Bitmap CheckPictureByUri(String uri) {
		// TODO Auto-generated method stub
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(new URL(uri).openStream());
			return bitmap;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ͨ��·���鿴��Ƶ
	 * @param path
	 * @return
	 */
	public String CheckAudioByPath(String path) {
		// TODO Auto-generated method stub
		File audioFile = new File(path);
		return audioFile.getAbsolutePath();
	}

	/**
	 * ͨ�������ַ��Uri�鿴��Ƶ
	 * @param uri
	 * @return
	 */
	public String CheckAudioByUri(String uri) {
		// TODO Auto-generated method stub
		return uri;
	}

	/**
	 * ͨ��·���鿴��Ƶ
	 * @param path
	 * @return
	 */
	public boolean CheckVideoByPath(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * ͨ�������ַ��Uri�鿴��Ƶ
	 * @param uri
	 * @return
	 */
	public boolean CheckVideoByUri(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

}
