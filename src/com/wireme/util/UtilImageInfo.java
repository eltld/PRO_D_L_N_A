package com.wireme.util;

import java.util.ArrayList;

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
	 * mImageList	图片列表
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
			builder.setMessage("存储列表为空...").setPositiveButton("确定", null);
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
		
		System.out.println("共有" + mImageList.size() + "张图片");
		return mImageList;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
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
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}
