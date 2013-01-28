package com.cattsoft.multimediaviewer;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.brunjoy.video.R;
import com.cattsoft.util.UtilImpl;
import com.cattsoft.util.UtilInterf;

public class AudioViewer extends Activity {
	private MediaPlayer mMediaPlayer;
	private UtilInterf utilimpl;
	private int position;
	private String filename;
	private Button start;
	private Button stop;
	private Button resume;
	private Button restart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_viewer);

		mMediaPlayer = new MediaPlayer();
		utilimpl = new UtilImpl();
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		resume = (Button) findViewById(R.id.resume);
		restart = (Button) findViewById(R.id.restart);

		ButtonClickListener listener = new ButtonClickListener();
		start.setOnClickListener(listener);
		stop.setOnClickListener(listener);
		resume.setOnClickListener(listener);
		restart.setOnClickListener(listener);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Toast.makeText(this, "ͨ��˵�ѡ�����������", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.filename = savedInstanceState.getString("filename");
		this.position = savedInstanceState.getInt("position");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("filename", filename);
		outState.putInt("position", position);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {// ���ͻȻ�绰������ֹͣ��������
		// TODO Auto-generated method stub

		if (mMediaPlayer.isPlaying()) {
			position = mMediaPlayer.getCurrentPosition();// ���浱ǰ���ŵ�
			mMediaPlayer.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		if (position > 0 && filename != null) {// ���绰������������
			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(filename);
				mMediaPlayer.prepare();
				mMediaPlayer.start();// ����
				mMediaPlayer.seekTo(position);
				position = 0;
			} catch (IOException e) {
				Log.e("AudioViewer", e.toString());
			}
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_audio_player, menu);
		return true;
	}

	/**
	 * ����˵�ѡ�������Ӧ�Ķ���
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		String sdcardDir = Environment.getExternalStorageDirectory().getPath();

		if (item.getTitle().equals("From Path")) {
			try {
				filename = utilimpl.CheckAudioByPath(sdcardDir + "/180.mp3");
				Toast.makeText(AudioViewer.this, filename, Toast.LENGTH_SHORT)
						.show();
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(filename);
				mMediaPlayer.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		} else if (item.getTitle().equals("From Uri")) {
			try {
				filename = utilimpl
						.CheckAudioByUri("http://zhangmenshiting2.baidu.com/data2/music/14960324/14960324.mp3?xcode=ca512607e91fdd1bd21c0ed7334abdcd&mid=0.51751727786217");
				Toast.makeText(AudioViewer.this, filename, Toast.LENGTH_SHORT)
						.show();
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(filename);
				mMediaPlayer.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else if (item.getTitle().equals("�����б�")) {
			Intent intent = new Intent();
			intent.setClass(AudioViewer.this, AudioList.class);
			startActivity(intent);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class ButtonClickListener implements View.OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button button = (Button) v;
			try {
				switch (v.getId()) {
				case R.id.start:// ����
					mMediaPlayer.start();
					break;
				case R.id.stop:// ��������ڲ��ŵĻ���������ֹͣ
					if (mMediaPlayer.isPlaying())
						mMediaPlayer.stop();
					break;
				case R.id.resume:
					if (mMediaPlayer.isPlaying()) {
						mMediaPlayer.pause();// ��ͣ����
						button.setText("����");// �������ť�ϵ�������ʾΪ����
					} else {
						mMediaPlayer.start();// �����
						button.setText("��ͣ");// �������ť�ϵ�������ʾΪ��ͣ
					}
					break;
				case R.id.restart:
					if (mMediaPlayer.isPlaying()) {
						mMediaPlayer.seekTo(0);// �����0��ʼ����
					} else {
						// �����û�в��ţ�������ʼ����
						mMediaPlayer.reset();
						mMediaPlayer.setDataSource(filename);
						mMediaPlayer.prepare();
						mMediaPlayer.start();
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
