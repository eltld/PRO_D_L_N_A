package com.cattsoft.multimediaviewer;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.brunjoy.video.R;

public class VideoPlayer extends Activity {
	private boolean isStop = false;
	private TextView playtime = null;
	private TextView durationTime = null;
	private SeekBar seekbar = null;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private File videofile;
	private String videoPath;
	private MediaPlayer mMediaPlayer;
	private int position;
	private int currentPosition;
	private ImageButton playbutton;
	private ImageButton backwardbutton;
	private ImageButton forwardbutton;
	private int mScreenHeight, mScreenWidth;
	private Builder builder;
	private AlertDialog alert;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ���ر�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ǿ�ƺ���
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// ��ȡ��Ļ����
		WindowManager wm = (WindowManager) this
				.getSystemService(this.WINDOW_SERVICE);
		mScreenHeight = wm.getDefaultDisplay().getHeight();
		mScreenWidth = wm.getDefaultDisplay().getWidth();

		setContentView(R.layout.activity_video_play);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		Bundle bundle = this.getIntent().getExtras();
		videoPath = bundle.getString("videoPath");
	}

	private void initView() {
		/**
		 * ��ʼ������
		 */
		mMediaPlayer = new MediaPlayer();
		playtime = (TextView) findViewById(R.id.playtime);
		durationTime = (TextView) findViewById(R.id.duration);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setFixedSize(mScreenWidth, (int) (mScreenHeight * 0.8));

		/**
		 * ��ʼ����ť�ͼ�����
		 */
		ButtonClickListener listener = new ButtonClickListener();
		playbutton = (ImageButton) this.findViewById(R.id.playBtn);
		backwardbutton = (ImageButton) this.findViewById(R.id.backward);
		forwardbutton = (ImageButton) this.findViewById(R.id.forward);
		playbutton.setOnClickListener(listener);
		backwardbutton.setOnClickListener(listener);
		forwardbutton.setOnClickListener(listener);

		/**
		 * ��ʼ�������
		 */
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser)
					mMediaPlayer.seekTo(progress);
			}
		});

		/**
		 * ע�ᵱsurfaceView�������ı�����ʱӦ��ִ�еķ���
		 */
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {

			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mMediaPlayer != null)
					mMediaPlayer.release();
				myHandler.removeMessages(1);
			}

			public void surfaceCreated(SurfaceHolder holder) {
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});

		/**
		 * ����MediaPlayer�������
		 */
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {

			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				builder = new AlertDialog.Builder(VideoPlayer.this);
				builder.setMessage("���Ŵ��󣬿��ܸ�ʽ����֧�֡�").setPositiveButton("֪����",
						null);
				alert = builder.create();
				alert.show();
				return false;
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		try{
			loadData();
			initView();
		}catch(Exception E){
			Toast.makeText(VideoPlayer.this, "ͨ��˵�ѡ�����������", Toast.LENGTH_SHORT).show();
		}
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_video_player, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getTitle().equals("From Path")) {
			Toast.makeText(VideoPlayer.this, "�ݲ�֧��", Toast.LENGTH_SHORT).show();
		} else if (item.getTitle().equals("From Uri")) {
			Toast.makeText(VideoPlayer.this, "�ݲ�֧��", Toast.LENGTH_SHORT).show();
		} else if (item.getTitle().equals("�����б�")) {
			Intent intent = new Intent();
			intent.setClass(VideoPlayer.this, VideoList.class);
			startActivity(intent);
			VideoPlayer.this.finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mMediaPlayer != null)
				mMediaPlayer.release();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		position = savedInstanceState.getInt("position");
		String path = savedInstanceState.getString("path");
		if (path != null && !"".equals(path)) {
			videofile = new File(path);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("position", position);
		if (videofile != null)
			outState.putString("path", videofile.getAbsolutePath());

		super.onSaveInstanceState(outState);
	}

	private final class ButtonClickListener implements View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.playBtn:
				if (mMediaPlayer.isPlaying()) {
					pause();
				} else {
					play();
				}
				break;
			case R.id.backward:
				backword();
				break;
			case R.id.forward:
				forward();
				break;
			default:
				break;
			}
		}
	}

	private void play() {
		try {
			// �ı䰴ť���
			playbutton.setBackgroundResource(R.drawable.pause_selecor);
			// ��ȡҪ���ŵ��ļ�

			// ����MediaPlayer����
			mMediaPlayer.reset();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDisplay(surfaceHolder);
			mMediaPlayer.setDataSource(videoPath);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			System.out.println(videoPath);
			// ����һ��
			setup();
			// ��ʼ����
			mMediaPlayer.start();
		} catch (Exception e) {
			System.out.println("play is wrong");
		}
	}

	private void pause() {
		playbutton.setBackgroundResource(R.drawable.play_selecor);
		mMediaPlayer.pause();
	}

	private void backword() {
		Toast.makeText(VideoPlayer.this, "�ݲ�֧��", Toast.LENGTH_SHORT).show();
	}

	private void forward() {
		Toast.makeText(VideoPlayer.this, "�ݲ�֧��", Toast.LENGTH_SHORT).show();
	}

	private void setup() {
		try {
			mMediaPlayer.prepare();
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(final MediaPlayer mp) {
					seekbar.setMax(mp.getDuration());
					myHandler.sendEmptyMessage(1);
					playtime.setText(toTime(mp.getCurrentPosition()));
					durationTime.setText(toTime(mp.getDuration()));
					mp.seekTo(currentPosition);
				}
			});
		} catch (Exception e) {
			System.out.println("prepare is wrong");
		}
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				try {
					if (mMediaPlayer != null && !isStop) {
						currentPosition = mMediaPlayer.getCurrentPosition();
					}
					seekbar.setProgress(currentPosition);
					playtime.setText(toTime(currentPosition));
					myHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	};

	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		return String.format("%02d:%02d", minute, second);
	}
}
