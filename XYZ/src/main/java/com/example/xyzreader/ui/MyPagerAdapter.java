package com.example.xyzreader.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;

/**
 * Extracted from ArticleDetailActivity on 2/14/2017.
 */

public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static final String TAG = MyPagerAdapter.class.getSimpleName();

    private Cursor mCursor;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        Log.d(TAG, "MyPagerAdapter created");
    }

    public void setCursor(Cursor cursor) {
        Log.d(TAG, "setCursor()");
        mCursor = cursor;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Log.d(TAG, "MyPagerAdapter setPrimaryItem()");

    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "MyPagerAdapter getItem()");
        mCursor.moveToPosition(position);
        return ArticleDetailFragment.newInstance(
                mCursor.getLong(ArticleLoader.Query._ID), position);

    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }
}
