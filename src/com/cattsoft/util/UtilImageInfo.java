package com.cattsoft.util;

import java.util.ArrayList;
import com.cattsoft.entity.Image;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class UtilImageInfo {
	/**
	 * mImageList	ͼƬ�б�
	 */
	private ArrayList<Image> mImageList = new ArrayList<Image>();

	private Context mContext;
	private Builder builder;
	private AlertDialog alert;

	public UtilImageInfo(Context context) {
		mContext = context;
	}

	public ArrayList<Image> getImageList() {
		Image temp = null;
		Cursor cur = mContext.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID,
						MediaStore.Images.Media.DISPLAY_NAME,
						MediaStore.Images.Media.TITLE,
						MediaStore.Images.Media.DESCRIPTION,
						MediaStore.Images.Media.SIZE,
						MediaStore.Images.Media.DATA },
				MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg", "image/jpg", "image/png",
						"image/bmp", "image/gif" }, null);
		
		if (cur == null || cur.getCount() == 0) {
			builder = new AlertDialog.Builder(mContext);
			builder.setMessage("�洢�б�Ϊ��...").setPositiveButton("ȷ��", null);
			alert = builder.create();
			alert.show();
			return null;
		}
		
		cur.moveToFirst();
		int size = cur.getCount();
		for (int i = 0; i < size; i++) {
			temp = new Image();
			
			temp.setmFileName(cur.getString(2));
			temp.setmFilePath(cur.getString(5));
			if (cur.getString(4) != null) {// fileSize
				float t = cur.getInt(4) / 1024f / 1024f;
				String sizeStr = (t + "").substring(0, 4);
				temp.setmFileSize(sizeStr + "M");
			} else {
				temp.setmFileSize("undefine");
			}
			temp.setmImageID(cur.getInt(0));
			temp.setmImageName(cur.getString(2));
			temp.setmThumbnail(getImageThumbnail(cur.getString(5), 60, 60));
			
			mImageList.add(temp);
			cur.moveToNext();
		}
		
		System.out.println("����" + mImageList.size() + "��ͼƬ");
		return mImageList;
	}

	/**

	 * @param imagePath
	 *            ͼ���·��
	 * @param width
	 *            ָ�����ͼ��Ŀ��
	 * @param height
	 *            ָ�����ͼ��ĸ߶�
	 * @return ��ɵ�����ͼ
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // ��Ϊ false
		// �������ű�
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}
