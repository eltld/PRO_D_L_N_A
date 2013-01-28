package com.cattsoft.multimediaviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.brunjoy.video.R;

public class VideoViewer extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_video_player, menu);
        return true;
    }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getTitle().equals("From Path")){
			Toast.makeText(VideoViewer.this, "�ݲ�֧��", Toast.LENGTH_SHORT).show();
		}else if(item.getTitle().equals("From Uri")){
			Toast.makeText(VideoViewer.this, "�ݲ�֧��", Toast.LENGTH_SHORT).show();
		}else if(item.getTitle().equals("�����б�")){
			Intent intent = new Intent();
			intent.setClass(VideoViewer.this, VideoList.class);
			startActivity(intent);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
