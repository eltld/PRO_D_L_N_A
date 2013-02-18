package com.brunjoy.recommend;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brunjoy.duanluo.imgdowner.ImageDownloader;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.video.R;
import com.wireme.util.JsonAnalysis;
import com.wireme.util.MyDataManager;
import com.wireme.util.MyDataManager.MyCallBacke;
import com.wireme.util.RecommendHolder;

public class RecommendAPPActivity extends Activity {

    private ListView mListView;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.recommend_app );
        mListView = (ListView) findViewById( R.id.recommend_listView );
        initData( );
        getApkList( );
    }

    private MyBaseAdapter adapter;

    private void initData() {
        adapter = new MyBaseAdapter( );
        mListView.setAdapter( adapter );
        mListView.setCacheColorHint( Color.TRANSPARENT );
    }

    private void getApkList() {
        MyDataManager.getInstance( this ).getApkList( new MyCallBacke( ) {

            @Override
            public void resultMsg(String resultMSG) {
                MyLog.d( "hai", "resultMsg  resultMSG=" + resultMSG );
                JsonAnalysis mAnalysis = new JsonAnalysis( );
                list.addAll( mAnalysis.analySisDownJson( resultMSG ) );

                adapter.notifyDataSetChanged( );
            }

            @Override
            public void failureMsg(String msg) {

            }
        } );
    }

    ArrayList<RecommendHolder> list = new ArrayList<RecommendHolder>( );

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size( );
        }

        @Override
        public Object getItem(int position) {
            return list.get( position );
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if (view == null)
                view = getLayoutInflater( ).inflate( R.layout.recommend_item, null );
            ImageView image = (ImageView) view.findViewById( R.id.item_recommend_thumb );
            TextView name = (TextView) view.findViewById( R.id.item_recommend_Name );
            TextView other = (TextView) view.findViewById( R.id.item_recommend_Other );
            TextView v = (TextView) view.findViewById( R.id.item_recommend_version );
            RecommendHolder holder = (RecommendHolder) getItem( position );
            ImageDownloader.getInstacne( ).download( holder.getIcon( ), image );
            name.setText( holder.getName( ) );
            v.setText( "v"+holder.getVersion( ) );
            other.setText( holder.getDesc( )  );
            MyLog.d( "hai", holder.toString( ) );
            return view;
        }

        public void clear() {
            if (list != null)
                list.clear( );

        }

    }
}
