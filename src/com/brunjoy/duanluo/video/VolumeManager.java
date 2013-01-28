package com.brunjoy.duanluo.video;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VolumeManager {

	private AudioManager myAudioManager = null;
	private static VolumeManager mVolumeManager;

	// 单例模式
	public synchronized static VolumeManager getInstance(Context mContext) {
		if (mVolumeManager == null) {
			mVolumeManager = new VolumeManager(mContext);
			
		}
		return mVolumeManager;
	}

	public void clear() {
		mVolumeManager = null;
		myAudioManager = null;
		mSeekBar=null;
	}

	// 私有化
	private VolumeManager(Context mContext) {
		myAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		
	}

	public static int mode = AudioManager.MODE_CURRENT;

	public int getVolumeMode() {
		return myAudioManager.getMode();
	}

	public int getMaxVolum() {
		return myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public void SetVolume(int num) {
		if (num < 0) {
			num = 0;
		}
		if (num > myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
			num = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		
		myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, num,
				AudioManager.FLAG_PLAY_SOUND);
		mSeekBar.setProgress(num);
	}

	public void setMute(boolean flag) {
		myAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, flag);
		Log.e("hai", "set mute=" + flag);
	}

	public int getCurrentVolum() {

		return myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	private SeekBar mSeekBar;
	public void setSeekBar(SeekBar mSeekBar) {
		if (mSeekBar == null)
			return;
		this.mSeekBar=mSeekBar;
		mSeekBar.setMax(getMaxVolum());
		mSeekBar.setProgress(getCurrentVolum());
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				SetVolume(progress);
			}
		});
	}	
	

	public void VolumeDown() {
		SetVolume(getCurrentVolum()-1);
		
	}

	public void VolumeUp() {
		SetVolume(getCurrentVolum()+1);
		
	}

}
