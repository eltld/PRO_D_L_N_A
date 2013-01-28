package com.brunjoy.weiget;

import java.net.URI;
import java.net.URISyntaxException;

import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunjoy.duanluo.imgdowner.ImageDownloader;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.video.R;
import com.brunjoy.video.activity.ContentItem;

public class MyDataListManager {
    public enum FILE_TYPE {
        VIDEO, IMAGE, AUDIO
    };

    private static MyDataListManager mMyDataListManager;
    private MyArrayAdapter<ContentItem> contentListAdapter;
    String TAG = "MyDataListManager";

    public MyDataListManager(final Context mContext) {
        contentListAdapter = new MyArrayAdapter<ContentItem>( mContext ) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null) {
                    view = LayoutInflater.from( mContext ).inflate( R.layout.item, null );
                }
                ImageView image = (ImageView) view.findViewById( R.id.item_thunm );
                TextView name = (TextView) view.findViewById( R.id.itemName );
                TextView other = (TextView) view.findViewById( R.id.itemOther );
                image.setImageResource( R.drawable.ablum_deflaut );
                ContentItem mContentItem = getItem( position );
                if (mContentItem.isContainer( )) {
                    image.setImageResource( R.drawable.folder );
                    Container mContainer = mContentItem.getContainer( );
                    String extraInfo = "";
                    if (mContainer.getChildCount( ) != null) {
                        extraInfo = "(" + mContainer.getChildCount( ) + ")";
                    }
                    name.setText( mContainer.getTitle( ) + extraInfo );
                    other.setText( mContainer.getId( ) + "  " + mContainer.getParentID( ) + "  " + mContainer.getChildCount( ) );
                } else {
                    Item mItem = mContentItem.getItem( );
                    if (mItem instanceof VideoItem) {
                        VideoItem item = (VideoItem) mItem;
                        if (item.getAlbumArtURI( ) != null)
                            ImageDownloader.getInstacne( ).download( item.getAlbumArtURI( ).toString( ), image );
                        name.setText( item.getTitle( ) );
                        other.setText( item.getCreator( ) );
                    } else if (mItem instanceof ImageItem) {
                        ImageItem item = (ImageItem) mItem;
                        if (mItem.getFirstResource( ) != null) {
                            try {
                                URI myUri = new URI( mItem.getFirstResource( ).getValue( ) );
                                MyLog.d( TAG, "=======================myUri.getPath( )=" + myUri.getPath( ) + "  getHost=" + myUri.getHost( ) );
                                ImageDownloader.getInstacne( ).download( myUri.toString( ), image );
                            } catch (URISyntaxException e) {
                                e.printStackTrace( );
                            }
                        }
                        name.setText( item.getTitle( ) + "" );
                        other.setText( item.getCreator( ) );
                    } else if (mItem instanceof MusicTrack) {
                        MusicTrack item = (MusicTrack) mItem;
                        ImageDownloader.getInstacne( ).download( item.getAlbumArtURI( ) == null ? null : item.getAlbumArtURI( ).toString( ), image );
                        name.setText( item.getTitle( ) );
                        other.setText( item.getCreator( ) );
                    } else {
                        image.setImageBitmap( null );
                        name.setText( "不清楚的资源" );
                        other.setText( "不清楚的资源" );
                    }
                }
                return view;
            }
        };
    }

    public MyArrayAdapter getMyArrayAdapter() {
        return contentListAdapter;
    }

    public static MyDataListManager getInstance(Context mContext) {
        if (mMyDataListManager == null) {
            mMyDataListManager = new MyDataListManager( mContext );
        }
        return mMyDataListManager;
    }

    private static int positon;

    public void setPosition(int positon) {
        MyDataListManager.positon = positon;
    }

    public int getSize() {
        return contentListAdapter.getCount( );
    }

    public ContentItem getItem(int position) {
        return contentListAdapter.getItem( position );
    }

    public Item getNext(FILE_TYPE type) {
        for (int i = 1, lg = getSize( ); i <= lg; i++) {
            int index = (positon + i) % lg;
            Item item = getItem( index ).getItem( );
            if (type == FILE_TYPE.AUDIO) {
                if (item instanceof MusicTrack) {
                    setPosition( index );
                    return item;
                }
            } else if (type == FILE_TYPE.IMAGE) {
                if (item instanceof ImageItem) {
                    setPosition( index );
                    return item;
                }
            } else if (type == FILE_TYPE.VIDEO) {
                if (item instanceof VideoItem) {
                    setPosition( index );
                    return item;
                }
            }
        }
        return null;
    }

    public Item getLast(FILE_TYPE type) {
        for (int i = 1, lg = getSize( ); i <= lg; i++) {
            int index = (positon - i+lg) % lg;
            Item item = getItem( index ).getItem( );
            if (type == FILE_TYPE.AUDIO) {
                if (item instanceof MusicTrack) {
                    setPosition( index );
                    return item;
                }
            } else if (type == FILE_TYPE.IMAGE) {
                if (item instanceof ImageItem) {
                    setPosition( index );
                    return item;
                }
            } else if (type == FILE_TYPE.VIDEO) {
                if (item instanceof VideoItem) {
                    setPosition( index );
                    return item;
                }
            }
        }
        return null;
    }
}
