package com.cattsoft.util;

/**
 * UtilSongInfo
 * 
 * this class is used to get the message of the assign audio file from database  
 * 
 * @author jianqiao-liu
 */
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.brunjoy.video.R;
import com.cattsoft.entity.Audio;

public class UtilAudioInfo {
	/**
	 * mSongsList �����б�
	 */
	private ArrayList<Audio> mSongsList = new ArrayList<Audio>();

	/**
	 * mContext
	 */
	private Context mContext = null;

	private Builder builder;
	private AlertDialog alert;

	/**
	 * ר������
	 */
	private Bitmap albumPic;

	/**
	 * SongInfoUtils()
	 * 
	 * @param aContext
	 */
	public UtilAudioInfo(Context aContext) {
		mContext = aContext;
	}

	/**
	 * getFileInfo()
	 * 
	 * @param aFileAbsoulatePath
	 *            �ļ����·��
	 * @return ���������Ϣ�ַ�����
	 */
	public String[] getFileInfo(String aFileAbsoulatePath) {
		String[] fileMessage = new String[4];
		File file = new File(aFileAbsoulatePath);
		String fileName = file.getName();
		String filePath = aFileAbsoulatePath;
		System.out.println("�ļ�·����" + filePath);
		System.out.println("�ļ���" + fileName);
		if (file.exists()) {
			if (mContext != null) {

				getSongList();

				int count = mSongsList.size();
				for (int i = 0; i < count; i++) {
					if (mSongsList.get(i).getmFilePath().equals(filePath)
							&& mSongsList.get(i).getmFileName()
									.equals(fileName)) {
						fileMessage[0] = mSongsList.get(i).getmFileTitle();
						fileMessage[1] = mSongsList.get(i).getmAlbum();
						fileMessage[2] = mSongsList.get(i).getmSinger();
						fileMessage[3] = toTime(mSongsList.get(i)
								.getmDuration());
						albumPic = mSongsList.get(i).getmAlbumPhoto();
						break;
					}
				}
			}
		} else {
			System.out.println("�ļ�������");
		}

		return fileMessage;
	}

	public Bitmap getAlbumPhoto(String aFileAbsoulatePath) {
		if (albumPic == null) {
			getFileInfo(aFileAbsoulatePath);
		}
		return albumPic;
	}

	/**
	 * getSongList() ���ظ����б�
	 * 
	 * @param cursor
	 */
	public ArrayList<Audio> getSongList() {
		Audio song = null;
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA },
				MediaStore.Audio.Media.MIME_TYPE + "=? or "
						+ MediaStore.Audio.Media.MIME_TYPE + "=?",
				new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
		if (cursor == null || cursor.getCount() == 0) {
			builder = new AlertDialog.Builder(mContext);
			builder.setMessage("�洢�б�Ϊ��...").setPositiveButton("ȷ��", null);
			alert = builder.create();
			alert.show();
			return null;
		}
		cursor.moveToFirst();
		int size = cursor.getCount();
		for (int i = 0; i < size; i++) {
			String data = cursor.getString(1);
			if (data.endsWith("mp3") || data.endsWith("MP3")
					|| data.endsWith("wma") || data.endsWith("WMA")) {
				Log.d("checkData", data);

				song = new Audio();
				song.setmSongID(cursor.getInt(0));// song id
				song.setmFileName(cursor.getString(1));// file Name
				song.setmFileTitle(cursor.getString(2));// song name
				song.setmDuration(cursor.getLong(3));// play time
				song.setmSinger(cursor.getString(4));// artist
				song.setmAlbum(cursor.getString(5));// album
				if (cursor.getString(6) != null) {// year
					song.setmYear(cursor.getString(6));
				} else {
					song.setmYear("undefine");
				}
				if ("audio/mpeg".equals(cursor.getString(7).trim())) {// file
																		// type
					song.setmFileType("mp3");
				} else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
					song.setmFileType("wma");
				}
				if (cursor.getString(8) != null) {// fileSize
					float temp = cursor.getInt(8) / 1024f / 1024f;
					String sizeStr = (temp + "").substring(0, 4);
					song.setmFileSize(sizeStr + "M");
				} else {
					song.setmFileSize("undefine");
				}

				if (cursor.getString(9) != null) {// file path
					song.setmFilePath(cursor.getString(9));
				}

				// ����ר������
				song.setmAlbumPhoto(getArtworkFromFile(mContext,
						cursor.getInt(0)));

				mSongsList.add(song);
			}
			cursor.moveToNext();
		}
		System.out.println("����" + mSongsList.size() + "�׸�");
		return mSongsList;
	}

	/**
	 * ���ļ��ж�ȡר��ͼƬ,��������ʹ��Ĭ��ͼƬ
	 * @param context
	 * @param songid
	 * @return
	 */
	private Bitmap getArtworkFromFile(Context context, long songid) {
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.ALBUM_ID }, "_id=?",
				new String[] { songid + "" }, null);
		cur.moveToFirst();
		int albumId = cur.getInt(0);
		Bitmap bm = null;
		if (albumId < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album id or a song id");
		}
		if (albumId < 0 && songid > 0) {
			// ֱ�Ӵ��ļ���ȡר��ͼƬ
			System.out.println("���ļ��ж�ȡר��ͼƬ");
			try {

				BitmapFactory.Options options = new BitmapFactory.Options();

				FileDescriptor fd = null;
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}

				options.inSampleSize = 1;
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fd, null, options);
				options.inSampleSize = 500;
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;

				bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
			} catch (FileNotFoundException ex) {

			}
			if (bm != null) {
				return bm;
			}
		}

		// ��MediaStore��ݿ��ж�ȡר��ͼƬ
		System.out.println("��MediaStore��ݿ��ж�ȡר��ͼƬ");
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(
				Uri.parse("content://media/external/audio/albumart"), albumId);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();

				options.inSampleSize = 1;

				options.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(in, null, options);

				options.inSampleSize = computeSampleSize(options, 180);

				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or maybe it never existed to begin with.

				bm = getDefaultArtwork(context);
				return bm;

			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}
		return bm;
	}

	/**
	 *  ��ȡĬ��ר������ͼƬ
	 * @param context
	 * @return
	 */
	public Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.ablum_deflaut), null, opts);
	}

	/**
	 * ʱ���ʽת������
	 * @param time
	 * @return
	 */
	public String toTime(long time) {

		time /= 1000;
		long minute = time / 60;
		long second = time % 60;
		return String.format("%02d:%02d", minute, second);
	}

	/**
	 * ����ר��ͼƬ��С
	 * @param options
	 * @param target
	 * @return
	 */
	public int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		Log.v("ADW", "candidate:" + candidate);
		return candidate;
	}
}
