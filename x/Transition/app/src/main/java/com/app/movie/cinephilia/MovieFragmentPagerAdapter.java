package com.app.movie.cinephilia;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by GAURAV on 19-04-2016.
 */
public class MovieFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Popular", "Highest Rated", "Favorites" };
    private int[] imgResId = {
            R.drawable.ic_trending_up_white_18dp,
            R.drawable.ic_thumb_up_white_18dp,
            R.drawable.ic_favorite_white_18dp
    };
    private Context context;

    public MovieFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setTextColor(context.getResources().getColor(R.color.lightGrey));
        tv.setText(tabTitles[position]);
        //tv.setTextColor(ContextCompat.getColor(this.context, R.color.colorAccent));
        ImageView img = (ImageView) v.findViewById(R.id.imgView);
        img.setImageResource(imgResId[position]);
        return v;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return GridViewFragment.newInstance(position + 1);
    }

    /*
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Drawable image = ContextCompat.getDrawable(context, imgResId[position]);;
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }*/
}
