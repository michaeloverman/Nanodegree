package tech.michaeloverman.android.popularmovies;

import java.net.URL;

/**
 * Simple class to hold the title and url of each video link.
 * Created by Michael on 12/20/2016.
 */

public class VideoLink {
    private String mTitle;
    private URL mURL;
    
    public VideoLink(String title, URL url) {
        mTitle = title;
        mURL = url;
    }
    public String getTitle() {
        return mTitle;
    }
    
    public URL getURL() {
        return mURL;
    }
}
