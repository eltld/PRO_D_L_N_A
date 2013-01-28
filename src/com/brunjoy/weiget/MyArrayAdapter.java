package com.brunjoy.weiget;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.brunjoy.video.R;

public abstract class MyArrayAdapter<T> extends ArrayAdapter<T> {
    
    public MyArrayAdapter(Context context) {
        super( context, R.layout.item, new ArrayList<T>( ) );
    }
    @Override
    public abstract View getView(int position, View view, ViewGroup parent);

}
