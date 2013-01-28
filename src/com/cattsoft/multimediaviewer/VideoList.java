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
import com.cattsoft.entity.Video;
import com.cattsoft.util.UtilVideoInfo;

public class VideoList extends Activity {
	/** Called when the activity is first created. */
	private ListView listview;
	public UtilVideoInfo videoInfoTool;
	public ArrayList<Video> mVideoList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list_view);
		loadData();
		initView();
	}
	
	private void loadData(){
		videoInfoTool = new UtilVideoInfo(this);
		mVideoList = videoInfoTool.getVideoList();
	}
	
	private void initView(){
		listview = (ListView)findViewById(R.id.video_list_view);
		listview.setAdapter(new VideoListAdapter(this,mVideoList));
		listview.setOnItemClickListener(new ListItemClickListener());
	}

	class ListItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(VideoList.this, VideoPlayer.class);
			intent.putExtra("videoPath", mVideoList.get(position).getmFilePath());
			startActivity(intent);
		}

	}
}
