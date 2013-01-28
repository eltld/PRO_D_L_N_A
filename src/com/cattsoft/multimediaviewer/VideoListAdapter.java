package com.cattsoft.multimediaviewer;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunjoy.video.R;
import com.cattsoft.entity.Video;

public class VideoListAdapter extends BaseAdapter {

	private Context myCon;
	private ArrayList<Video> mVideoList;

	public VideoListAdapter(Context con, ArrayList<Video> list) {
		myCon = con;
		mVideoList = list;
	}

	public int getCount() {
		return mVideoList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(myCon).inflate(R.layout.video_list_item,
				null);
		Video temp = mVideoList.get(position);
		
		//��Ƶ����ͼ
		ImageView thumbutil = (ImageView)convertView.findViewById(R.id.thumbutil);
		thumbutil.setImageBitmap(temp.getmThumbnail());
		
		//��Ƶ���
		TextView tv_video = (TextView) convertView.findViewById(R.id.video_name);
		tv_video.setText(temp.getmFileTitle());
		
		//��Ƶʱ��
		TextView tv_time = (TextView) convertView.findViewById(R.id.time);
		tv_time.setText(toTime(temp.getmDuration()));
		
		//��Ƶ�ļ���С
		TextView tv_size = (TextView)convertView.findViewById(R.id.size);
		tv_size.setText(temp.getmFileSize());
		
		return convertView;
	}

	public String toTime(long time) {
		time /= 1000;
		long minute = time / 60;
		long second = time % 60;
		if(minute >= 60){
			long hour = minute / 60;
			minute %= 60;
			return String.format("%02d:%02d:%02d", hour,minute,second);
		}
		return String.format("%02d:%02d", minute, second);
	}
}
