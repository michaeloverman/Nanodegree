package com.app.movie.cinephilia;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by GAURAV on 11-06-2016.
 */
public class ImageTransformation implements Transformation {

    private int mTargetWidth, mTargetHeight;

    public ImageTransformation(int width){
        mTargetWidth = width;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        double aspectRatio = (double) source.getHeight()/(double) source.getWidth();
        mTargetHeight = (int) (mTargetWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(source, mTargetWidth, mTargetHeight, false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "cropPosterTransformation" + mTargetHeight;
    }
}
