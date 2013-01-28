package com.brunjoy.weiget;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.brunjoy.video.R;

public abstract class MainActivity extends SherlockFragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate( savedInstanceState );
        map.clear( );
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_main );
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager( ) );

        mViewPager = (ViewPager) findViewById( R.id.pager );
        mViewPager.setAdapter( mSectionsPagerAdapter );

        mViewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener( ) {
            @Override
            public void onPageSelected(int position) {
                MainActivity.this.onPageSelected( position );
            }
        } );
        
    }
    public abstract void onPageSelected(int position);
    public int getCurrentItemId() {
        return mViewPager.getCurrentItem( );
    }

    public void setCurrentItem(int index) {
        mViewPager.setCurrentItem( index );
    }

    @Deprecated
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView( layoutResID );
    }

    @Deprecated
    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView( view, params );
    }

    @Deprecated
    @Override
    public void setContentView(View view) {
        super.setContentView( view );
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super( fm );
        }
        @Override
        public Fragment getItem(int i) {
            if (map.containsKey( i )) {
                return map.get( i );
            } else {
                Fragment fragment = new DummySectionFragment( i );
                map.put( i, fragment );
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return "2";
            case 1:
                return "2";
            case 2:
                return "3";
            }
            return null;
        }
    }

    public abstract View onCreateDefView(int index, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    private static HashMap<Integer, Fragment> map = new HashMap<Integer, Fragment>( );

    @Override
    protected void onRestart() {
        map.clear( );
        super.onRestart( );
    }

    @Override
    protected void onPause() {
        map.clear( );
        super.onPause( );
    }

    @Override
    protected void onStop() {
        map.clear( );
        super.onStop( );
    }

    @Override
    protected void onDestroy() {

        super.onDestroy( );
        map.clear( );
        mSectionsPagerAdapter = null;
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class DummySectionFragment extends Fragment {
        private int index;

        public DummySectionFragment(int index) {
            super( );
            this.index = index;

        }

        public DummySectionFragment() {
            super( );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return onCreateDefView( index, inflater, container, savedInstanceState );

        }
    }
}
