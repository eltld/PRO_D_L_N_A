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
import com.cattsoft.entity.Audio;

public class AudioListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Audio> mAudioList;
	
	public AudioListAdapter(Context context,ArrayList<Audio> list){
		mContext = context;
		mAudioList = list;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mAudioList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(mContext).inflate(R.layout.audio_list_item,
				null);
		Audio temp = mAudioList.get(position);
		
		//ר��ͼƬ����ͼ
		ImageView albumPic = (ImageView)convertView.findViewById(R.id.album_pic);
		albumPic.setImageBitmap(temp.getmAlbumPhoto());
		
		//������
		TextView name = (TextView)(TextView)convertView.findViewById(R.id.name);
		name.setText(temp.getmFileTitle());
		
		//����
		TextView artist = (TextView)convertView.findViewById(R.id.artist);
		artist.setText(temp.getmSinger());
		
		//����ʱ��
		TextView duration = (TextView)convertView.findViewById(R.id.duration);
		duration.setText(toTime(temp.getmDuration()));
		
		//�����С
		TextView size = (TextView)convertView.findViewById(R.id.size);
		size.setText(temp.getmFileSize());
		
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
