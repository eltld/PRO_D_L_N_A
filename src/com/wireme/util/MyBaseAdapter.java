package com.wireme.util;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter extends BaseAdapter {
    private ArrayList list = new ArrayList( );

    // protected String flag = "1";
    //
    // public String getFlag() {
    // return flag;
    // }
    //
    // public void setFlag(String flag) {
    // this.flag = flag;
    // }

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

    /**
     * 需要{@link bindDataSet  }
     */

    public abstract View getView(int position, View convertView);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView( position, convertView );
    }

    public void clear() {
        if (list != null)
            list.clear( );

    }

    public void add(Object contentItem) {
        if (list != null) {
            list.add( contentItem );
        }

    }

    public void addList(ArrayList list) {
        if (list != null) {
            list.addAll( list );
        }

    }

    //
    // private boolean container;
    //
    // public boolean isContainer() {
    // return this.container;
    // }
    //
    // public void setContainer(boolean container) {
    // this.container = container;
    // }

}
