package com.cattsoft.multimediaviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.brunjoy.video.R;

public class MainActivity extends Activity {

	private Button btn_picture;
	private Button btn_video;
	private Button btn_audio;
	private Builder builder;
	private AlertDialog alert;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		btn_picture = (Button) findViewById(R.id.picture_viewer);
		btn_video = (Button) findViewById(R.id.video_viewer);
		btn_audio = (Button) findViewById(R.id.audio_viewer);

		btn_picture.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ImagePlayer.class);
				startActivity(intent);
			}
		});

		btn_video.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, VideoPlayer.class);
				startActivity(intent);
			}
		});

		btn_audio.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AudioPlayer.class);
				startActivity(intent);
			}
		});
		
		builder = new Builder(this);
		builder.setTitle("ȷ���˳�");
		builder.setMessage("Exit Really ?");
		builder.setPositiveButton("Yes", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("No", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		alert = builder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getTitle().equals("EXIT")){
			alert.show();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			alert.show();
		}
		return super.onKeyDown(keyCode, event);
	}
}
