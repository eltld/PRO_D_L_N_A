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
import com.cattsoft.entity.Image;

public class ImageListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Image> mImageList;
	
	public ImageListAdapter(Context context,ArrayList<Image> list){
		mContext = context;
		mImageList = list;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mImageList.size();
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
		convertView = LayoutInflater.from(mContext).inflate(R.layout.image_list_item, null);
		Image temp = mImageList.get(position);
		
		//ͼƬ����ͼ
		ImageView thumbutil = (ImageView)convertView.findViewById(R.id.image_thumbutil);
		thumbutil.setImageBitmap(temp.getmThumbnail());
		
		//����ͼƬ���
		TextView name = (TextView)convertView.findViewById(R.id.image_name);
		if(temp.getmImageName().length() > 15){
			name.setText(temp.getmImageName().substring(0, 15) + "...");
		}else{
			name.setText(temp.getmImageName());
		}
		
		//����ͼƬ·��
		TextView path = (TextView)convertView.findViewById(R.id.image_path);
		if(temp.getmFilePath().length() > 15){
			path.setText("λ�ã�" + temp.getmFilePath().substring(0,15) + "...");
		}else{
			path.setText("λ�ã�" + temp.getmFilePath());
		}
		
		//����ͼƬ��С
		TextView size = (TextView)convertView.findViewById(R.id.size);
		size.setText(temp.getmFileSize());
		
		return convertView;
	}

}
