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
import com.cattsoft.entity.Image;
import com.cattsoft.util.UtilImageInfo;

public class ImageList extends Activity {
	private ListView mListView;
	private UtilImageInfo imageInfoTool;
	public static ArrayList<Image> mImageList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_list_view);
		loadData();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView)findViewById(R.id.image_list_view);
		mListView.setAdapter(new ImageListAdapter(ImageList.this, mImageList));
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("index", arg2);
				intent.setClass(ImageList.this, ImagePlayer.class);
				startActivity(intent);
			}
		});
	}

	private void loadData() {
		// TODO Auto-generated method stub
		imageInfoTool = new UtilImageInfo(this);
		mImageList = imageInfoTool.getImageList();
	}
}
