package com.cattsoft.multimediaviewer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.brunjoy.video.R;
import com.cattsoft.entity.Audio;
import com.cattsoft.util.UtilAudioInfo;

public class AudioList extends Activity {
	private ListView mListView;
	private UtilAudioInfo songInfoTool;
	public static ArrayList<Audio> mSongList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_list_view);
		loadData();
		initView();
	}

	private void loadData(){
		songInfoTool = new UtilAudioInfo(AudioList.this);
		mSongList = songInfoTool.getSongList();
	}
	
	private void initView(){
		mListView = (ListView)findViewById(R.id.audio_list);
		mListView.setAdapter(new AudioListAdapter(AudioList.this, mSongList));
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("index", arg2);
				intent.setClass(AudioList.this, AudioPlayer.class);
				startActivity(intent);
			}
		});
	}
}
